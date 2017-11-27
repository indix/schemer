package schemer

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.{IntegerType, StringType, StructField, StructType}
import org.scalatest._

class CSVSchemaSpec extends FlatSpec with Matchers {
  implicit val spark: SparkSession = SparkSession.builder
    .config(new SparkConf())
    .master("local[*]")
    .getOrCreate()

  "CSVSchema" should "infer schema from given path" in {
    val path = getClass.getClassLoader.getResource("test.csv").getPath

    val inferredSchema = CSVSchema().infer(path)
    val fields         = inferredSchema.fields

    fields.length should be(3)
    fields(0).name should be("title")
    fields(0).`type` should be("string")

    fields(1).name should be("url")
    fields(1).`type` should be("string")

    fields(2).name should be("storeId")
    fields(2).`type` should be("int")
  }

  it should "infer schema without header from file" in {
    val path = getClass.getClassLoader.getResource("test.csv").getPath

    val inferredSchema = CSVSchema(CSVOptions(false)).infer(path)
    val fields         = inferredSchema.fields

    fields.length should be(3)
    fields(0).name should be("_c0")
    fields(0).`type` should be("string")

    fields(1).name should be("_c1")
    fields(1).`type` should be("string")

    fields(2).name should be("_c2")
    fields(2).`type` should be("string")
  }

  it should "infer schema and read" in {
    val path = getClass.getClassLoader.getResource("test.csv").getPath

    val inferredSchema = CSVSchema().infer(path)
    import spark.implicits._
    val output = inferredSchema.toDf(path).as[TestRecord].collect()

    output.length should be(3)
    output(0).title should be("iphone")
    output(0).url should be("http://indix.com/iphone")
    output(0).storeId should be(42)
  }

  it should "infer schema and get schema json" in {
    val path = getClass.getClassLoader.getResource("test.csv").getPath

    val inferredSchema = CSVSchema().infer(path)

    inferredSchema.schema() should be(
      "{\"fields\":[{\"name\":\"title\",\"nullable\":true,\"type\":\"string\",\"position\":0},{\"name\":\"url\",\"nullable\":true,\"type\":\"string\",\"position\":1},{\"name\":\"storeId\",\"nullable\":true,\"type\":\"int\",\"position\":2}],\"options\":{\"header\":true,\"headerBasedParser\":false,\"separator\":\",\",\"quoteChar\":\"\\\"\",\"escapeChar\":\"\\\\\"}}"
    )
  }

  it should "get schema from json" in {
    val schema = CSVSchema(
      "{\"fields\":[{\"name\":\"title\",\"nullable\":true,\"type\":\"string\",\"position\":0},{\"name\":\"url\",\"nullable\":true,\"type\":\"string\",\"position\":1},{\"name\":\"storeId\",\"nullable\":true,\"type\":\"int\",\"position\":2}],\"options\":{\"header\":true,\"headerBasedParser\":false,\"separator\":\",\",\"quoteChar\":\"\\\"\",\"escapeChar\":\"\\\\\"}}"
    )

    schema.sparkSchema() should be(
      StructType(
        Seq(
          StructField("title", StringType, true),
          StructField("url", StringType, true),
          StructField("storeId", IntegerType, true)
        )
      )
    )
  }
}
