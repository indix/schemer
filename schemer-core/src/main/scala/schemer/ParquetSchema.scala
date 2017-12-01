package schemer

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType
import scala.reflect.runtime.universe._

case class ParquetSchemaBase[T <: SchemaLike: TypeTag](override val options: Map[String, String] = Map())
    extends SchemaLikeBase[ParquetSchema] {
  override def infer(paths: String*)(implicit spark: SparkSession) = {
    val schema = spark.read.parquet(paths: _*).schema
    val underlyingSchema = typeOf[T] match {
      case t if t =:= typeOf[AvroSchema] => ("avro", AvroSchema(schema))
      case t if t =:= typeOf[JSONSchema] => ("json", JSONSchema(schema))
      case t if t =:= typeOf[CSVSchema]  => ("csv", CSVSchema(schema, options))
    }

    ParquetSchema(underlyingSchema._2.schema(), underlyingSchema._1)
  }
}

case class ParquetSchema(schema: String, `type`: String) extends SchemaLike {

  val schemaType = `type` match {
    case "avro" => AvroSchema(schema)
    case "csv"  => CSVSchema(schema)
    case "json" => JSONSchema(schema)
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
    case "avro" => apply[AvroSchema]()
    case "csv"  => apply[CSVSchema]()
    case "json" => apply[JSONSchema]()
  }
}
