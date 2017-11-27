package schemer

import org.apache.spark.SparkConf
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.scalatest.{FlatSpec, Matchers}

class ParquetSchemaSpec extends FlatSpec with Matchers {
  implicit val spark: SparkSession = SparkSession.builder
    .config(new SparkConf())
    .master("local[*]")
    .getOrCreate()

  "ParquetSchema" should "infer avro schema" in {
    import spark.implicits._
    val df = Seq(TestRecord("iphone", "http://indix.com/iphone", 42)).toDF

    val dataDir = "test_parquet_avro"

    try {
      df.write.mode(SaveMode.Overwrite).parquet(dataDir)
      val schema = ParquetSchema[AvroSchema]().infer(dataDir)
      schema.schema.replaceAll("SchemerInferred_[^\"]+", "SchemerInferred") should be(
        "{\n  \"type\" : \"record\",\n  \"name\" : \"SchemerInferred\",\n  \"namespace\" : \"schemer\",\n  \"fields\" : [ {\n    \"name\" : \"title\",\n    \"type\" : [ \"string\", \"null\" ]\n  }, {\n    \"name\" : \"url\",\n    \"type\" : [ \"string\", \"null\" ]\n  }, {\n    \"name\" : \"storeId\",\n    \"type\" : [ \"int\", \"null\" ]\n  } ]\n}"
      )
    } finally {
      Helpers.cleanOutputPath(dataDir)
    }
  }

  it should "infer json schema" in {
    import spark.implicits._
    val df = Seq(TestRecord("iphone", "http://indix.com/iphone", 42)).toDF

    val dataDir = "test_parquet_json"

    try {
      df.write.mode(SaveMode.Overwrite).parquet(dataDir)
      val schema = ParquetSchema[JSONSchema]().infer(dataDir)
      schema.schema.replaceAll("SchemerInferred_[^\"]+", "SchemerInferred") should be(
        "{\"type\":\"object\",\"properties\":{\"title\":{\"type\":\"string\"},\"url\":{\"type\":\"string\"},\"storeId\":{\"type\":\"integer\"}},\"additionalProperties\":false}"
      )
    } finally {
      Helpers.cleanOutputPath(dataDir)
    }
  }

  it should "infer csv schema" in {
    import spark.implicits._
    val df = Seq(TestRecord("iphone", "http://indix.com/iphone", 42)).toDF

    val dataDir = "test_parquet_csv"

    try {
      df.write.mode(SaveMode.Overwrite).parquet(dataDir)
      val schema = ParquetSchema[CSVSchema]().infer(dataDir)
      schema.schema.replaceAll("SchemerInferred_[^\"]+", "SchemerInferred") should be(
        "{\"fields\":[{\"name\":\"title\",\"nullable\":true,\"type\":\"string\",\"position\":0},{\"name\":\"url\",\"nullable\":true,\"type\":\"string\",\"position\":1},{\"name\":\"storeId\",\"nullable\":true,\"type\":\"int\",\"position\":2}],\"options\":{\"header\":true,\"headerBasedParser\":true,\"separator\":\",\",\"quoteChar\":\"\\\"\",\"escapeChar\":\"\\\\\"}}"
      )
    } finally {
      Helpers.cleanOutputPath(dataDir)
    }
  }
}
