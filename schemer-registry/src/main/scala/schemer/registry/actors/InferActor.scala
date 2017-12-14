package schemer.registry.actors

import akka.actor.{Actor, ActorRef, ActorSystem, Status}
import akka.event.Logging
import akka.pattern.pipe
import akka.util.Timeout
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import schemer._

import scala.concurrent.Future
import scala.util.{Failure, Random, Success}

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
) extends Actor {
  import context.dispatcher
  val logger = Logging(context.system, this)

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
    logger.info(s"Starting inference for jobGroup $jobGroup")

    val inferFuture = Future {
      spark.sparkContext.setJobGroup(jobGroup, jobGroup, true)
      block
    } recoverWith {
      case ex =>
        logger.info(s"Inference for jobGroup $jobGroup failed - ${ex.getMessage}")
        Future.failed(SchemerInferenceException(ex.getMessage))
    }

    inferFuture onComplete {
      case Success(r) =>
        logger.info(s"Completing inference for jobGroup $jobGroup")
        sender ! r
      case Failure(f) =>
        sender ! Status.Failure(f)
    }

    system.scheduler.scheduleOnce(inferTimeout.duration) {
      logger.info(s"Cancelling jobGroup $jobGroup")
      spark.sparkContext.cancelJobGroup(jobGroup)
    }

  }

  override def preStart(): Unit =
    logger.info(s"Starting infer actor")
}
