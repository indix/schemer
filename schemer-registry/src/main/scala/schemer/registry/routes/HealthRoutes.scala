package schemer.registry.routes

import akka.http.scaladsl.server.Directives.{complete, get, path}

trait HealthRoutes {
  val healthRoutes = path("health") {
    get {
      complete {
        "OK"
      }
    }
  }
}
