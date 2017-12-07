package schemer.registry

import io.getquill.{PostgresAsyncContext, SnakeCase}

package object sql {
  type DbContext = PostgresAsyncContext[SnakeCase]
}
