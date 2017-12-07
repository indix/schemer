package schemer.registry.sql

import com.typesafe.config.Config
import schemer.registry.server.ConfigWithDefault

trait DatabaseConfig extends ConfigWithDefault {
  def rootConfig: Config

  val h2config       = rootConfig.getConfig("h2")
  val postgresConfig = rootConfig.getConfig("postgres")
}
