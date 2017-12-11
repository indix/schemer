package schemer.registry.graphql

import org.apache.spark.sql.SparkSession
import sangria.macros.derive.GraphQLField
import schemer._
import schemer.registry.dao.SchemaDao
import schemer.registry.models.Schema
import schemer.registry.utils.Clock

import scala.concurrent.{ExecutionContext, Future}

case class SchemerInferenceException(message: String) extends Exception(message)

class GraphQLService(
    schemaDao: SchemaDao
)(implicit val spark: SparkSession, implicit val clock: Clock, implicit val ec: ExecutionContext) {

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

  @GraphQLField
  def addSchema(name: String, namespace: String, `type`: String, user: String) =
    schemaDao.create(Schema.withRandomUUID(name, namespace, `type`, clock.nowUtc, user))

  def handleException(f: Future[Any]) = f.recoverWith {
    case ex: Exception =>
      Future.failed(SchemerInferenceException(ex.getMessage))
  }
}
