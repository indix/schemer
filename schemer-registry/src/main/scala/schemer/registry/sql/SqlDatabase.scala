package schemer.registry.sql

import io.getquill.{PostgresAsyncContext, SnakeCase}
import org.apache.commons.lang3.StringUtils
import org.flywaydb.core.Flyway

case class SqlDatabase(config: DatabaseConfig) {
  lazy val ctx = new PostgresAsyncContext(SnakeCase, config.postgresConfig)

  def updateSchema() = {
    val postgresUrl = config.postgresConfig.getString("url")
    if (StringUtils.isNotEmpty(postgresUrl)) {
      val flyway = new Flyway()
      flyway.setOutOfOrder(true)
      flyway.setDataSource(s"jdbc:$postgresUrl", "", "")
      flyway.migrate()
    }
  }
}
