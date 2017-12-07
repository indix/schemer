package schemer.registry.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import com.typesafe.scalalogging.StrictLogging

trait Routes extends GraphQLRoutes with HealthRoutes with StrictLogging {
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
        graphQLRoutes ~ healthRoutes
      }
    }
  }
}
