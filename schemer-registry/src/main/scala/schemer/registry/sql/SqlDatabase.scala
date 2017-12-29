package schemer.registry.sql

import io.getquill.{PostgresAsyncContext, SnakeCase}
import org.apache.commons.lang3.StringUtils
import org.flywaydb.core.Flyway
import org.joda.time.DateTime

trait Quotes { this: PostgresAsyncContext[_] =>
  implicit class DateTimeQuotes(l: DateTime) {
    def >(r: DateTime) = quote(infix"$l > $r".as[Boolean])
    def <(r: DateTime) = quote(infix"$l < $r".as[Boolean])
  }

  implicit class OptDateTimeQuotes(l: Option[DateTime]) {
    def >(r: DateTime) = quote(infix"($l::timestamptz is null) or $l > $r".as[Boolean])
    def <(r: DateTime) = quote(infix"$l::timestamptz is null or $l < $r".as[Boolean])
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
