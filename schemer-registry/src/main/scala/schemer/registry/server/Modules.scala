package schemer.registry.server

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.StrictLogging
import schemer.registry.graphql.GraphQLService
import schemer.registry.utils.RealTimeClock

import scala.concurrent.ExecutionContext

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
