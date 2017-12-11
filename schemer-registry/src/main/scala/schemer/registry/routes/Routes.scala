package schemer.registry.routes

import akka.http.scaladsl.model.{StatusCodes, Uri}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, RejectionHandler}
import com.typesafe.scalalogging.StrictLogging

trait Routes extends GraphQLRoutes with HealthRoutes with StrictLogging {
  private val exceptionHandler = ExceptionHandler {
    case e: Exception =>
      logger.error(s"Exception during client request processing: ${e.getMessage}", e)
      _.complete((StatusCodes.InternalServerError, "Internal server error"))
  }
  val rejectionHandler  = RejectionHandler.default
  val logBlackListPaths = Seq("health")
  private def isBlacklistedPath(uri: Uri) =
    logBlackListPaths
      .map(s"/" + _)
      .exists(uri.toString().contains)
  val logDuration = extractRequestContext.flatMap { ctx =>
    val start = System.currentTimeMillis()
    mapResponse { resp =>
      val d = System.currentTimeMillis() - start
      if (!isBlacklistedPath(ctx.request.uri)) {
        logger.info(s"[${resp.status.intValue()}] ${ctx.request.method.name} ${ctx.request.uri} took: ${d}ms")
      }
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
