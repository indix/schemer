package schemer.registry.dao

import java.util.UUID

import schemer.registry.models.{Schema, SchemaVersion}
import schemer.registry.sql.SqlDatabase

import scala.concurrent.{ExecutionContext, Future}

class SchemaDao(val db: SqlDatabase)(implicit val ec: ExecutionContext) {
  import db.ctx._

  val schemas                              = quote(querySchema[Schema]("schemas"))
  def find(id: UUID)                       = run(schemas.filter(c => c.id == lift(id))).map(_.headOption)
  def create(schema: Schema): Future[UUID] = run(schemas.insert(lift(schema)).returning(_.id))

  val schemaVersions = quote(querySchema[SchemaVersion]("schema_versions"))

  def createVersion(schemaVersion: SchemaVersion): Future[UUID] =
    run(schemaVersions.insert(lift(schemaVersion)).returning(_.id))

  def findVersion(id: UUID, version: String) = {
    val query = quote {
      schemas
        .filter(_.id == lift(id))
        .join(schemaVersions.filter(_.version == lift(version)))
        .on((schema: Schema, version: SchemaVersion) => {
          schema.id == version.schemaId
        })
    }

    run(query).map(_.headOption)
  }
}
