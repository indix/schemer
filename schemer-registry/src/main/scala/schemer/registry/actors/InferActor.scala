package schemer.registry.actors

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.pipe
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import schemer._

import scala.concurrent.Future

case class SchemerInferenceException(message: String)
    extends Exception(s"Error while trying to infer schema - $message")
case class JSONSchemaInferenceRequest(paths: Seq[String])
case class AvroSchemaInferenceRequest(paths: Seq[String])
case class ParquetSchemaInferenceRequest(`type`: String, paths: Seq[String])
case class CSVSchemaInferenceRequest(options: CSVOptions, paths: Seq[String])

class InferActor(implicit val spark: SparkSession, implicit val system: ActorSystem) extends Actor with StrictLogging {
  import context.dispatcher

  def receive = {
    case JSONSchemaInferenceRequest(paths) =>
      handleException(sender()) {
        JSONSchema().infer(paths: _*)
      }
    case AvroSchemaInferenceRequest(paths) =>
      handleException(sender()) {
        AvroSchema().infer(paths: _*)
      }
    case CSVSchemaInferenceRequest(options, paths) =>
      handleException(sender()) {
        CSVSchema(options).infer(paths: _*)
      }
    case ParquetSchemaInferenceRequest(t, paths) =>
      handleException(sender()) {
        ParquetSchema(t).infer(paths: _*)
      }
    case _ => logger.info("Unsupported infer request")
  }

  def handleException(sender: ActorRef)(block: => Any) =
    Future {
      block
    } recoverWith {
      case ex =>
        Future.failed(SchemerInferenceException(ex.getMessage))
    } pipeTo sender
}
