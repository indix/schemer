package schemer.registry.graphql.schema

import sangria.schema._
import schemer.registry.graphql.schema.SchemaDefinition.constantComplexity
import sangria.macros.derive.{deriveInputObjectType, deriveObjectType, InputObjectTypeName}
import schemer.registry.graphql._
import spray.json.DefaultJsonProtocol
import sangria.marshalling.sprayJson._
import schemer._

trait JSONSchemaType {
  implicit val JSONSchemaType = ObjectType(
    "JSONSchema",
    "JSON Schema",
    fields[Unit, JSONSchema](
      Field(
        "schema",
        StringType,
        description = Some("CSV Schema as JSON string"),
        complexity = constantComplexity(10),
        resolve = ctx => ctx.value.schema
      ),
      Field(
        "sparkSchema",
        StringType,
        description = Some("Spark Schema as JSON string"),
        complexity = constantComplexity(100),
        resolve = ctx => ctx.value.sparkSchema().prettyJson
      )
    )
  )
}

trait InferType extends JSONSchemaType with DefaultJsonProtocol {
  lazy implicit val TypeArg             = Argument("type", ParquetSchemaUnderlyingType)
  lazy implicit val PathsArg            = Argument("paths", ListInputType(StringType))
  implicit val CSVOptionsFormat         = jsonFormat5(CSVOptions.apply)
  lazy implicit val CSVOptionsInputType = deriveInputObjectType[CSVOptions](InputObjectTypeName("CSVOptionsInput"))
  lazy implicit val CSVOptionsArg       = Argument("csvOptions", OptionInputType(CSVOptionsInputType), CSVOptions())

  lazy implicit val CSVFieldType   = deriveObjectType[Unit, CSVField]()
  lazy implicit val CSVOptionsType = deriveObjectType[Unit, CSVOptions]()
  lazy val CSVSchemaType = ObjectType(
    "CSVSchema",
    "CSV Schema",
    fields[Unit, CSVSchema](
      Field(
        "fields",
        ListType(CSVFieldType),
        description = Some("Fields of the CSV Schema"),
        complexity = constantComplexity(1),
        resolve = ctx => ctx.value.fields
      ),
      Field(
        "options",
        CSVOptionsType,
        description = Some("Options of the CSV Schema"),
        complexity = constantComplexity(1),
        resolve = ctx => ctx.value.options
      ),
      Field(
        "schema",
        StringType,
        description = Some("CSV Schema as JSON string"),
        complexity = constantComplexity(100),
        resolve = ctx => ctx.value.schema()
      ),
      Field(
        "sparkSchema",
        StringType,
        description = Some("Spark Schema as JSON string"),
        complexity = constantComplexity(100),
        resolve = ctx => ctx.value.sparkSchema().prettyJson
      )
    )
  )

  lazy val ParquetSchemaUnderlyingType = EnumType(
    "ParquetSchemaType",
    Some("Supported schema types for Parquet"),
    List(
      EnumValue("Avro", value = schemer.ParquetSchemaType.Avro.`type`),
      EnumValue("Csv", value = schemer.ParquetSchemaType.Csv.`type`),
      EnumValue("Json", value = schemer.ParquetSchemaType.Json.`type`)
    )
  )

  lazy val ParquetSchemaType = ObjectType(
    "ParquetSchema",
    "Parquet Schema",
    fields[Unit, ParquetSchema](
      Field(
        "type",
        ParquetSchemaUnderlyingType,
        description = Some("Parquet Schema type"),
        complexity = constantComplexity(10),
        resolve = ctx => ctx.value.`type`.`type`
      ),
      Field(
        "schema",
        StringType,
        description = Some("Parquet Schema as JSON string"),
        complexity = constantComplexity(10),
        resolve = ctx => ctx.value.schema
      ),
      Field(
        "sparkSchema",
        StringType,
        description = Some("Spark Schema as JSON string"),
        complexity = constantComplexity(100),
        resolve = ctx => ctx.value.sparkSchema().prettyJson
      )
    )
  )

  lazy val AvroSchemaType = ObjectType(
    "AvroSchema",
    "Avro Schema",
    fields[Unit, AvroSchema](
      Field(
        "schema",
        StringType,
        description = Some("Avro Schema as string"),
        complexity = constantComplexity(10),
        resolve = ctx => ctx.value.schema
      ),
      Field(
        "sparkSchema",
        StringType,
        description = Some("Spark Schema as JSON string"),
        complexity = constantComplexity(100),
        resolve = ctx => ctx.value.sparkSchema().prettyJson
      )
    )
  )

  lazy val InferType = ObjectType(
    "Inference",
    "Schema Inference",
    fields[GraphQLService, Unit](
      Field(
        "csv",
        CSVSchemaType,
        description = Some("CSV Schema inference"),
        complexity = constantComplexity(500),
        resolve = ctx => InferCSVSchemaDeferred(ctx arg CSVOptionsArg, ctx arg PathsArg),
        arguments = List(CSVOptionsArg, PathsArg)
      ),
      Field(
        "json",
        JSONSchemaType,
        description = Some("JSON Schema inference"),
        complexity = constantComplexity(500),
        resolve = ctx => InferJSONSchemaDeferred(ctx arg PathsArg),
        arguments = List(PathsArg)
      ),
      Field(
        "parquet",
        ParquetSchemaType,
        description = Some("Parquet Schema inference"),
        complexity = constantComplexity(500),
        resolve = ctx => InferParquetSchemaDeferred(ctx arg TypeArg, ctx arg PathsArg),
        arguments = List(TypeArg, PathsArg)
      ),
      Field(
        "avro",
        AvroSchemaType,
        description = Some("Avro Schema inference"),
        complexity = constantComplexity(500),
        resolve = ctx => InferAvroSchemaDeferred(ctx arg PathsArg),
        arguments = List(PathsArg)
      )
    )
  )
}
