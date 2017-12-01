package schemer

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType

import scala.reflect.runtime.universe._

sealed trait ParquetSchemaType {
  val `type`: String
}

object ParquetSchemaType {
  case object Avro extends ParquetSchemaType {
    override val `type`: String = "avro"
  }
  case object Csv extends ParquetSchemaType {
    override val `type`: String = "csv"
  }
  case object Json extends ParquetSchemaType {
    override val `type`: String = "json"
  }

  val supportedTypes = List(Avro, Csv, Json).map(_.`type`)
}

case class ParquetSchemaBase[T <: SchemaLike: TypeTag](override val options: Map[String, String] = Map())
    extends SchemaLikeBase[ParquetSchema] {
  override def infer(paths: String*)(implicit spark: SparkSession) = {
    val schema = spark.read.parquet(paths: _*).schema
    val underlyingSchema = typeOf[T] match {
      case t if t =:= typeOf[AvroSchema] => (ParquetSchemaType.Avro, AvroSchema(schema))
      case t if t =:= typeOf[JSONSchema] => (ParquetSchemaType.Json, JSONSchema(schema))
      case t if t =:= typeOf[CSVSchema]  => (ParquetSchemaType.Csv, CSVSchema(schema, options))
    }

    ParquetSchema(underlyingSchema._2.schema(), underlyingSchema._1)
  }
}

case class ParquetSchema(schema: String, `type`: ParquetSchemaType) extends SchemaLike {

  val schemaType = `type` match {
    case ParquetSchemaType.Avro => AvroSchema(schema)
    case ParquetSchemaType.Csv  => CSVSchema(schema)
    case ParquetSchemaType.Json => JSONSchema(schema)
  }

  override def validate = schemaType.validate

  def toDf(paths: String*)(implicit spark: SparkSession) =
    spark.read
      .schema(sparkSchema())
      .parquet(paths: _*)

  override def sparkSchema(): StructType = schemaType.sparkSchema()
}

object ParquetSchema {
  def apply[T <: SchemaLike: TypeTag]() = ParquetSchemaBase[T]()
  def apply(`type`: String) = `type` match {
    case ParquetSchemaType.Avro.`type` => apply[AvroSchema]()
    case ParquetSchemaType.Csv.`type`  => apply[CSVSchema]()
    case ParquetSchemaType.Json.`type` => apply[JSONSchema]()
  }
}
