package schemer.registry.dao

import java.util.UUID

import schemer.registry.models.Schema
import schemer.registry.sql.SqlDatabase

import scala.concurrent.{ExecutionContext, Future}

class SchemaDao(val db: SqlDatabase)(implicit val ec: ExecutionContext) {
  import db.ctx._

  val schemas                              = quote(querySchema[Schema]("schemas"))
  def find(id: UUID)                       = run(schemas.filter(c => c.id == lift(id))).map(_.headOption)
  def create(schema: Schema): Future[Long] = run(schemas.insert(lift(schema)))
}
