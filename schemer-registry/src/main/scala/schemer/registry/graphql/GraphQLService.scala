package schemer.registry.graphql

import org.apache.spark.sql.SparkSession
import schemer._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

case class SchemerInferenceException(message: String) extends Exception(message)

class GraphQLService(implicit val spark: SparkSession) {
  def inferCSVSchema(options: CSVOptions, paths: Seq[String]) =
    handleException(Future {
      CSVSchema(options).infer(paths: _*)
    })

  def inferJSONSchema(paths: Seq[String]) =
    handleException(Future {
      JSONSchema().infer(paths: _*)
    })

  def inferParquetSchema(`type`: String, paths: Seq[String]) =
    handleException(Future {
      ParquetSchema(`type`).infer(paths: _*)
    })

  def inferAvroSchema(paths: Seq[String]) =
    handleException(Future {
      AvroSchema().infer(paths: _*)
    })

  def handleException(f: Future[Any]) = f.recoverWith {
    case ex: Exception =>
      Future.failed(SchemerInferenceException(ex.getMessage))
  }
}
