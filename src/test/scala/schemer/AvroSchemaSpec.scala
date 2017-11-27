package schemer

import com.databricks.spark.avro._
import org.apache.spark.SparkConf
import org.apache.spark.sql.types.{IntegerType, StringType}
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.scalatest.{FlatSpec, Matchers}

class AvroSchemaSpec extends FlatSpec with Matchers {
  implicit val spark: SparkSession = SparkSession.builder
    .config(new SparkConf())
    .master("local[*]")
    .getOrCreate()

  "AvroSchema" should "infer avro schema from given path" in {
    import spark.implicits._
    val df = Seq(TestRecord("iphone", "http://indix.com/iphone", 42)).toDF

    try {
      df.write.mode(SaveMode.Overwrite).avro("test")
      val schema = AvroSchema().infer("test")
      schema.schema.replaceAll("SchemerInferred_[^\"]+", "SchemerInferred") should be(
        "{\n  \"type\" : \"record\",\n  \"name\" : \"SchemerInferred\",\n  \"namespace\" : \"schemer\",\n  \"fields\" : [ {\n    \"name\" : \"title\",\n    \"type\" : [ \"string\", \"null\" ]\n  }, {\n    \"name\" : \"url\",\n    \"type\" : [ \"string\", \"null\" ]\n  }, {\n    \"name\" : \"storeId\",\n    \"type\" : [ \"int\", \"null\" ]\n  } ]\n}"
      )
    } finally {
      Helpers.cleanOutputPath("test")
    }
  }

  it should "get spark schema" in {
    val schema = AvroSchema(
      "{\n  \"type\" : \"record\",\n  \"name\" : \"SchemerInferred\",\n  \"namespace\" : \"schemer\",\n  \"fields\" : [ {\n    \"name\" : \"title\",\n    \"type\" : [ \"string\", \"null\" ]\n  }, {\n    \"name\" : \"url\",\n    \"type\" : [ \"string\", \"null\" ]\n  }, {\n    \"name\" : \"storeId\",\n    \"type\" : [ \"int\", \"null\" ]\n  } ]\n}"
    )
    val schemaFields = schema.sparkSchema().fields
    schemaFields.length should be(3)

    schemaFields(0).name should be("title")
    schemaFields(0).dataType should be(StringType)

    schemaFields(1).name should be("url")
    schemaFields(1).dataType should be(StringType)

    schemaFields(2).name should be("storeId")
    schemaFields(2).dataType should be(IntegerType)
  }
}
