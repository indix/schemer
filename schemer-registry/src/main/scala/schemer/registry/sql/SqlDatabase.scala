package schemer.registry.sql

import io.getquill.{PostgresAsyncContext, SnakeCase}
import org.apache.commons.lang3.StringUtils
import org.flywaydb.core.Flyway
import org.joda.time.DateTime

trait Quotes { this: PostgresAsyncContext[_] =>
  implicit class DateTimeQuotes(left: DateTime) {
    def >(right: DateTime) = quote(infix"$left > $right".as[Boolean])
    def <(right: DateTime) = quote(infix"$left < $right".as[Boolean])
  }
}

case class SqlDatabase(config: DatabaseConfig) {
  lazy val ctx = new PostgresAsyncContext(SnakeCase, config.postgresConfig) with Quotes

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
