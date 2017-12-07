package schemer.registry.sql

import io.getquill.{PostgresAsyncContext, SnakeCase}
import org.flywaydb.core.Flyway

case class SqlDatabase(config: DatabaseConfig) {
  lazy val ctx = new PostgresAsyncContext(SnakeCase, config.postgresConfig)

  def updateSchema() = {
    val flyway = new Flyway()
    flyway.setOutOfOrder(true)
    flyway.setDataSource(s"jdbc:${config.postgresConfig.getString("url")}", "", "")
    flyway.migrate()
  }
}
