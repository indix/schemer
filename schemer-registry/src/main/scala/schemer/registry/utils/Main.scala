package schemer.registry.utils

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.StrictLogging

import scala.concurrent.ExecutionContext

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
    override def rootConfig: Config = loadDefault("aegis")
  }

  implicit lazy val clock = RealTimeClock
}

class Main {}
