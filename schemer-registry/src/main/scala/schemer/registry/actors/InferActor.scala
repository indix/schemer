package schemer.registry.actors

import akka.actor.{Actor, ActorRef, ActorSystem}
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import schemer._

import scala.util.{Failure, Success, Try}

case class SchemerInferenceException(message: String) extends Exception(message)
case class JSONSchemaInferenceRequest(paths: Seq[String])
case class AvroSchemaInferenceRequest(paths: Seq[String])
case class ParquetSchemaInferenceRequest(`type`: String, paths: Seq[String])
case class CSVSchemaInferenceRequest(options: CSVOptions, paths: Seq[String])

class InferActor(implicit val spark: SparkSession, implicit val system: ActorSystem) extends Actor with StrictLogging {

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

  def handleException(sender: ActorRef)(block: => Any) = Try(block) match {
    case Success(result) =>
      logger.info("Sending back inference result")
      sender ! result
    case Failure(e) =>
      val message = s"Error while trying to infer schema - ${e.getMessage}"
      logger.info(message)
      sender ! akka.actor.Status.Failure(new SchemerInferenceException(message))
  }
}
