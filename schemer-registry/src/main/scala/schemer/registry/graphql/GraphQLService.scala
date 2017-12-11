package schemer.registry.graphql

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import org.apache.spark.sql.SparkSession
import sangria.macros.derive.GraphQLField
import schemer._
import schemer.registry.dao.SchemaDao
import schemer.registry.models.Schema
import schemer.registry.utils.Clock

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

case class SchemerInferenceException(message: String) extends Exception(message)
case class JSONSchemaInferenceRequest(paths: Seq[String])
case class AvroSchemaInferenceRequest(paths: Seq[String])
case class ParquetSchemaInferenceRequest(`type`: String, paths: Seq[String])
case class CSVSchemaInferenceRequest(options: CSVOptions, paths: Seq[String])

class GraphQLService(
    schemaDao: SchemaDao,
    inferActor: ActorRef
)(implicit val spark: SparkSession, implicit val clock: Clock, implicit val ec: ExecutionContext) {

  implicit val timeout = Timeout(60 seconds)

  def inferCSVSchema(options: CSVOptions, paths: Seq[String]) =
    handleException(inferActor ? CSVSchemaInferenceRequest(options, paths))

  def inferJSONSchema(paths: Seq[String]) = handleException(inferActor ? JSONSchemaInferenceRequest(paths))

  def inferParquetSchema(`type`: String, paths: Seq[String]) =
    handleException(inferActor ? ParquetSchemaInferenceRequest(`type`, paths))

  def inferAvroSchema(paths: Seq[String]) = handleException(inferActor ? AvroSchemaInferenceRequest(paths))

  @GraphQLField
  def addSchema(name: String, namespace: String, `type`: String, user: String) =
    schemaDao.create(Schema.withRandomUUID(name, namespace, `type`, clock.nowUtc, user))

  def handleException(f: Future[Any]) = f.recoverWith {
    case ex: Exception =>
      Future.failed(SchemerInferenceException(ex.getMessage))
  }
}
