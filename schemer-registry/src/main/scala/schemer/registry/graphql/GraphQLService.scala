package schemer.registry.graphql

import akka.actor.{ActorRef, ActorSystem, Kill, Props}
import akka.pattern.{ask, AskTimeoutException}
import akka.util.Timeout
import org.apache.spark.sql.SparkSession
import sangria.macros.derive.GraphQLField
import schemer._
import schemer.registry.actors._
import schemer.registry.dao.SchemaDao
import schemer.registry.models.Schema
import schemer.registry.utils.Clock

import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class GraphQLService(
    schemaDao: SchemaDao,
    inferActor: ActorRef
)(
    implicit val spark: SparkSession,
    implicit val clock: Clock,
    implicit val ec: ExecutionContext,
    implicit val system: ActorSystem,
    implicit val inferTimeout: Timeout
) {

  def inferCSVSchema(options: CSVOptions, paths: Seq[String]) =
    inferWithActor(CSVSchemaInferenceRequest(options, paths))

  def inferJSONSchema(paths: Seq[String]) =
    inferWithActor(JSONSchemaInferenceRequest(paths))

  def inferParquetSchema(`type`: String, paths: Seq[String]) =
    inferWithActor(ParquetSchemaInferenceRequest(`type`, paths))

  def inferAvroSchema(paths: Seq[String]) =
    inferWithActor(AvroSchemaInferenceRequest(paths))

  @GraphQLField
  def addSchema(name: String, namespace: String, `type`: String, user: String) =
    schemaDao.create(Schema.withRandomUUID(name, namespace, `type`, clock.nowUtc, user))

  def inferWithActor(message: Any) =
    (inferActor ? message).recoverWith {
      case ex: SchemerInferenceException =>
        Future.failed(ex)
      case _: AskTimeoutException =>
        Future.failed(SchemerInferenceException("Timeout while trying to infer schema"))
      case ex =>
        Future.failed(SchemerInferenceException(ex.getMessage))
    }
}
