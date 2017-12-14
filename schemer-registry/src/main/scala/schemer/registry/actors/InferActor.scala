package schemer.registry.actors

import akka.actor.{Actor, ActorRef, ActorSystem}
import akka.pattern.pipe
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import schemer._

import scala.concurrent.Future
import scala.util.Random

case class SchemerInferenceException(message: String)
    extends Exception(s"Error while trying to infer schema - $message")
case class JSONSchemaInferenceRequest(paths: Seq[String])
case class AvroSchemaInferenceRequest(paths: Seq[String])
case class ParquetSchemaInferenceRequest(`type`: String, paths: Seq[String])
case class CSVSchemaInferenceRequest(options: CSVOptions, paths: Seq[String])

class InferActor(
    implicit val spark: SparkSession,
    implicit val system: ActorSystem,
    implicit val inferTimeout: Timeout
) extends Actor
    with StrictLogging {
  import context.dispatcher

  def receive = {
    case JSONSchemaInferenceRequest(paths) =>
      inferSchema(sender()) {
        JSONSchema().infer(paths: _*)
      }
    case AvroSchemaInferenceRequest(paths) =>
      inferSchema(sender()) {
        AvroSchema().infer(paths: _*)
      }
    case CSVSchemaInferenceRequest(options, paths) =>
      inferSchema(sender()) {
        CSVSchema(options).infer(paths: _*)
      }
    case ParquetSchemaInferenceRequest(t, paths) =>
      inferSchema(sender()) {
        ParquetSchema(t).infer(paths: _*)
      }
    case _ => logger.info("Unsupported infer request")
  }

  def inferSchema(sender: ActorRef)(block: => Any) = {
    val jobGroup = Random.alphanumeric take 12 mkString ""

    Future {
      spark.sparkContext.setJobGroup(jobGroup, jobGroup, false)
      block
    } recoverWith {
      case ex =>
        Future.failed(SchemerInferenceException(ex.getMessage))
    } pipeTo sender

    system.scheduler.scheduleOnce(inferTimeout.duration) {
      spark.sparkContext.cancelJobGroup(jobGroup)
    }

  }
}
