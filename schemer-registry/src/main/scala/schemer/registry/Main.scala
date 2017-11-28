package schemer.registry

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import akka.stream.{ActorMaterializer, Materializer}
import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.StrictLogging
import schemer.registry.graphql.GraphQLService
import schemer.registry.routes.{GraphQLRoutes, SwaggerRoutes}
import schemer.registry.utils.RealTimeClock

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait ServerConfig extends ConfigWithDefault {

  def rootConfig: Config

  lazy val serverHost: String = rootConfig.getString("server.host")
  lazy val serverPort: Int    = rootConfig.getInt("server.port")
}

trait Modules extends StrictLogging {

  implicit def system: ActorSystem

  implicit def ec: ExecutionContext

  implicit def mat: Materializer

  lazy val config = new ServerConfig {
    override def rootConfig: Config = loadDefault("registry")
  }

  implicit lazy val clock = RealTimeClock

  lazy val graphQLService = new GraphQLService()
}

trait Routes extends GraphQLRoutes with StrictLogging {
  private val exceptionHandler = ExceptionHandler {
    case e: Exception =>
      logger.error(s"Exception during client request processing: ${e.getMessage}", e)
      _.complete((StatusCodes.InternalServerError, "Internal server error"))
  }
  val rejectionHandler = RejectionHandler.default
  val logDuration = extractRequestContext.flatMap { ctx =>
    val start = System.currentTimeMillis()
    mapResponse { resp =>
      val d = System.currentTimeMillis() - start
      logger.info(s"[${resp.status.intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${d}ms")
      resp
    } & handleRejections(rejectionHandler)
  }
  val routes = logDuration {
    handleExceptions(exceptionHandler) {
      encodeResponse {
        graphQLRoutes
      }
    }
  }
}

class Main() extends StrictLogging {

  def start(): (Future[ServerBinding], Modules) = {

    implicit val _system       = ActorSystem("main")
    implicit val _materializer = ActorMaterializer()

    val modules = new Modules with Routes {
      implicit lazy val ec  = _system.dispatcher
      implicit lazy val mat = _materializer
      lazy val system       = _system

    }

    (Http().bindAndHandle(modules.routes, modules.config.serverHost, modules.config.serverPort), modules)
  }
}

object Main extends App with StrictLogging {
  val (startFuture, modules) = new Main().start()

  val host = modules.config.serverHost
  val port = modules.config.serverPort

  val system = modules.system

  startFuture.onComplete {
    case Success(b) =>
      logger.info(s"Server started on $host:$port")
      sys.addShutdownHook {
        b.unbind()
        shutdown()
      }
    case Failure(e) =>
      logger.error(s"Cannot start server on $host:$port", e)
      sys.addShutdownHook {
        shutdown()
      }
  }

  def shutdown() {
    modules.system.terminate()
    logger.info("Server stopped")
  }
}
