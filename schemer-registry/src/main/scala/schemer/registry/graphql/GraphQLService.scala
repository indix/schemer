package schemer.registry.graphql

import org.apache.spark.sql.SparkSession
import schemer.registry.graphql.schema.GCSVSchema
import schemer.{CSVOptions, CSVSchema}

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class GraphQLService(implicit val spark: SparkSession) {
  def inferCSVSchema(options: CSVOptions, paths: Seq[String]) = Future {
    val schema = CSVSchema(options).infer(paths: _*)
    GCSVSchema(schema.schema())
  }
}
