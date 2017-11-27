package schemer

import com.fasterxml.jackson.databind.JsonNode
import com.github.fge.jackson.JsonLoader
import com.github.fge.jsonschema.main.JsonSchemaFactory
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import schemer.utils.{JSONUtil, JsonSchemaValidationUtil}

import scala.annotation.tailrec
import scala.collection.JavaConverters._

abstract trait JSONSchemaNode {
  def toJSON: String = JSONUtil.toJson(this)
}

case class ObjectSchema(
    `type`: String = "object",
    properties: Map[String, JSONSchemaNode],
    additionalProperties: Boolean = false,
    $schema: Option[String] = None
) extends JSONSchemaNode

case class StringSchema(
    `type`: String = "string",
    format: Option[String] = None,
    pattern: Option[String] = None,
    minLength: Option[Int] = None,
    maxLength: Option[Int] = None
) extends JSONSchemaNode

case class IntegerSchema(`type`: String = "integer", minimum: Option[BigInt] = None, maximum: Option[BigInt] = None)
    extends JSONSchemaNode

case class NumberSchema(`type`: String = "number", minimum: Option[Double] = None, maximum: Option[Double] = None)
    extends JSONSchemaNode

case class BooleanSchema(`type`: String = "boolean") extends JSONSchemaNode

case class ArraySchema(`type`: String = "array", items: JSONSchemaNode) extends JSONSchemaNode

case class JSONSchemaBase() extends SchemaLikeBase[JSONSchema] {

  @tailrec
  private def processStructFields(
      fields: List[StructField],
      accum: List[(String, JSONSchemaNode)] = Nil
  ): List[(String, JSONSchemaNode)] =
    fields match {
      case x :: xs =>
        processStructFields(xs, accum ++ List(processField(x)))
      case Nil => accum
    }

  private def processField(x: StructField) =
    (x.name, processDataType(x.dataType))

  private def processDataType(dataType: DataType): JSONSchemaNode = dataType match {
    case StringType                      => StringSchema()
    case LongType | IntegerType          => IntegerSchema()
    case DoubleType                      => NumberSchema()
    case BooleanType                     => BooleanSchema()
    case f if f.isInstanceOf[StructType] => convertSparkToJsonSchema(dataType.asInstanceOf[StructType])
    case f if f.isInstanceOf[ArrayType] =>
      ArraySchema(items = processDataType(dataType.asInstanceOf[ArrayType].elementType))
  }

  def convertSparkToJsonSchema(schema: StructType, draft: Option[String] = None) =
    ObjectSchema(properties = processStructFields(schema.fields.toList).toMap, $schema = draft)

  override def infer(paths: String*)(implicit spark: SparkSession) = {
    val schema     = spark.read.json(paths: _*).schema
    val jsonSchema = convertSparkToJsonSchema(schema, Some("http://json-schema.org/draft-06/schema#")).toJSON
    JSONSchema(jsonSchema)
  }
}

case class JSONSchema(schema: String) extends SchemaLike {

  private val jsonSchema = JsonLoader.fromString(schema)

  override def validate: List[String] = {
    val validator    = JsonSchemaFactory.byDefault().getSyntaxValidator
    val report       = validator.validateSchema(jsonSchema)
    val syntaxErrors = JsonSchemaValidationUtil.process(report)
    if (syntaxErrors.isEmpty) {
      try {
        sparkSchema()
        List.empty
      } catch {
        case e: UnsupportedOperationException => List(e.getMessage)
      }
    } else {
      syntaxErrors
    }
  }

  override def sparkSchema(): StructType = jsonToStructType(jsonSchema).asInstanceOf[StructType]

  def toDf(paths: String*)(implicit spark: SparkSession) =
    spark.read
      .schema(sparkSchema())
      .json(paths: _*)

  private def getRequiredProps(jsonSchema: JsonNode) =
    if (jsonSchema.has("required") && jsonSchema.get("required").isArray) {
      Some(jsonSchema.get("required").elements().asScala.map(_.asText()))
    } else {
      None
    }

  private def toArrayType(field: JsonNode) = {
    val itemsNode = field.get("items")
    if (itemsNode != null && itemsNode.isArray) {
      ArrayType(jsonToStructType(itemsNode.get(0)))
    } else if (itemsNode != null && itemsNode.isObject) {
      ArrayType(jsonToStructType(itemsNode))
    } else {
      ArrayType(StringType)
    }
  }

  private def toObjectType(jsonSchema: JsonNode) = {
    val requiredFields = getRequiredProps(jsonSchema).getOrElse(List.empty)
    if (jsonSchema.has("patternProperties")) {
      MapType(
        StringType,
        jsonToStructType(jsonSchema.get("patternProperties").fields().asScala.toList.head.getValue)
      )
    } else {
      StructType(
        jsonSchema
          .get("properties")
          .fields()
          .asScala
          .toList
          .map(field => {
            val fieldType = jsonToStructType(field.getValue)
            StructField(field.getKey, fieldType, nullable = !requiredFields.toList.contains(field.getKey))
          })
      )
    }
  }

  private def jsonToStructType(jsonSchema: JsonNode): DataType =
    jsonSchema.get("type").asText() match {
      case "array"   => toArrayType(jsonSchema)
      case "object"  => toObjectType(jsonSchema)
      case "boolean" => BooleanType
      case "string"  => StringType
      case "integer" => LongType
      case "number"  => DoubleType
      case _ =>
        throw new UnsupportedOperationException(
          s"Trying to convert a unsupported type ${jsonSchema.get("type").asText()}.  Types other than (boolean, string, integer, number, object, array) aren't supported"
        )

    }
}

object JSONSchema {
  def apply(): JSONSchemaBase               = JSONSchemaBase()
  def apply(schema: StructType): JSONSchema = JSONSchema(JSONSchemaBase().convertSparkToJsonSchema(schema).toJSON)
}
