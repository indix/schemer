package schemer
import java.io.IOException

import com.databricks.spark.avro.SchemaConverters
import org.apache.avro.Schema.Parser
import org.apache.avro.SchemaBuilder
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType

import scala.util.Random

case class AvroSchemaBase() extends SchemaLikeBase {
  override def infer(paths: String*)(implicit spark: SparkSession) = {
    val schema = spark.read.format("com.databricks.spark.avro").load(paths: _*).schema

    AvroSchema(schema, s"SchemerInferred_${Random.alphanumeric take 12 mkString ""}", "schemer")
  }
}

case class AvroSchema(schemaJson: String) extends SchemaLike {

  private def avroSchema() = new Parser().parse(schema())

  override def validate =
    try {
      sparkSchema()
      List.empty
    } catch {
      case e: IOException => List(s"Error while consuming Avro schema: ${e.getMessage}")
    }

  override def sparkSchema() = SchemaConverters.toSqlType(avroSchema()).dataType.asInstanceOf[StructType]

  override def schema() = schemaJson

  override def toDf(paths: String*)(implicit spark: SparkSession) =
    spark.read.format("com.databricks.spark.avro").load(paths: _*)
}

object AvroSchema {
  def apply() = AvroSchemaBase()

  def apply(schema: StructType, record: String, namespace: String) = {
    val builder    = SchemaBuilder.record(record).namespace(namespace)
    val avroSchema = SchemaConverters.convertStructToAvro(schema, builder, "com.indix").toString(true)
    new AvroSchema(avroSchema)
  }
}
