package schemer

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._
import org.scalatest.{FlatSpec, Matchers}

class JSONSchemaSpec extends FlatSpec with Matchers {
  implicit val spark: SparkSession = SparkSession.builder
    .config(new SparkConf())
    .master("local[*]")
    .getOrCreate()

  "JSONSchema" should "infer json schema" in {
    val path = getClass.getClassLoader.getResource("test.json").getPath

    val inferredSchema = JSONSchema().infer(path)
    inferredSchema.schema should be(
      "{\"type\":\"object\",\"properties\":{\"imageUrls\":{\"type\":\"array\",\"items\":{\"type\":\"string\"}},\"url\":{\"type\":\"string\"},\"price\":{\"type\":\"object\",\"properties\":{\"max\":{\"type\":\"number\"},\"min\":{\"type\":\"number\"}},\"additionalProperties\":false},\"storeId\":{\"type\":\"integer\"},\"isAvailable\":{\"type\":\"boolean\"},\"title\":{\"type\":\"string\"}},\"additionalProperties\":false}"
    )

    val fields = inferredSchema.sparkSchema().fields
    fields.length should be(6)
    fields.map(f => (f.name, f.dataType)) should contain allElementsOf List(
      ("title", StringType),
      ("url", StringType),
      ("storeId", LongType),
      ("price", StructType(Seq(StructField("max", DoubleType), StructField("min", DoubleType)))),
      ("isAvailable", BooleanType),
      ("imageUrls", ArrayType(StringType))
    )
  }
}
