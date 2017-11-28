package schemer.registry.server

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.slf4j.StrictLogging
import schemer.registry.routes.Routes

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class Main() extends StrictLogging {

  def start(): (Future[ServerBinding], Modules) = {

    implicit val _system: ActorSystem             = ActorSystem("main")
    implicit val _materializer: ActorMaterializer = ActorMaterializer()

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
