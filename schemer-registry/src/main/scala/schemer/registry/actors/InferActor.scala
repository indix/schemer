package schemer.registry.actors

import akka.actor.{Actor, ActorSystem}
import com.typesafe.scalalogging.StrictLogging
import org.apache.spark.sql.SparkSession
import schemer.{AvroSchema, CSVSchema, JSONSchema, ParquetSchema}
import schemer.registry.graphql._

import scala.util.{Failure, Success, Try}

class InferActor(implicit val spark: SparkSession, implicit val system: ActorSystem) extends Actor with StrictLogging {

  def receive = {
    case JSONSchemaInferenceRequest(paths)         => handleException { JSONSchema().infer(paths: _*) }
    case AvroSchemaInferenceRequest(paths)         => handleException { AvroSchema().infer(paths: _*) }
    case CSVSchemaInferenceRequest(options, paths) => handleException { CSVSchema(options).infer(paths: _*) }
    case ParquetSchemaInferenceRequest(t, paths)   => handleException { ParquetSchema(t).infer(paths: _*) }
    case _                                         => logger.info("Unsupported infer request")
  }

  def handleException(block: => Any) = Try(block) match {
    case Success(result) => sender() ! result
    case Failure(e) =>
      val message = s"Error while trying to infer schema - ${e.getMessage}"
      logger.info(message)
      sender() ! akka.actor.Status.Failure(new SchemerInferenceException(message))
  }
}
