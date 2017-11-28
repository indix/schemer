package schemer.registry.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._

trait SwaggerRoutes {

  val swaggerRoutes = pathPrefix("swagger") {
    pathEnd {
      extractUri { uri =>
        redirect(uri + "/", StatusCodes.TemporaryRedirect)
      }
    } ~
      pathSingleSlash {
        getFromResource("swagger-ui/index.html")
      } ~
      getFromResourceDirectory("swagger-ui")
  }
}
