package schemer.registry.graphql

import org.apache.spark.sql.SparkSession
import schemer.{CSVOptions, CSVSchema, JSONSchema, ParquetSchema}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GraphQLService(implicit val spark: SparkSession) {
  def inferCSVSchema(options: CSVOptions, paths: Seq[String]) = Future {
    CSVSchema(options).infer(paths: _*)
  }

  def inferJSONSchema(paths: Seq[String]) = Future {
    JSONSchema().infer(paths: _*)
  }

  def inferParquetSchema(`type`: String, paths: Seq[String]) = Future {
    ParquetSchema(`type`).infer(paths: _*)
  }
}
