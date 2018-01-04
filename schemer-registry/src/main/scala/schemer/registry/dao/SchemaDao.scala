package schemer.registry.dao

import java.util.UUID

import org.joda.time.DateTime
import schemer.registry.models.{Schema, SchemaVersion}
import schemer.registry.sql.SqlDatabase

import scala.concurrent.{ExecutionContext, Future}

case class PaginatedFilter(
    id: Option[UUID],
    first: Option[Int],
    after: Option[DateTime],
    last: Option[Int],
    before: Option[DateTime]
) {
  def take = (last orElse first).filter(_ <= 10).getOrElse(10) + 1
}

class SchemaDao(val db: SqlDatabase)(implicit val ec: ExecutionContext) {
  import db.ctx._

  val schemas                              = quote(querySchema[Schema]("schemas"))
  def find(id: UUID)                       = run(schemas.filter(c => c.id == lift(id))).map(_.headOption)
  def create(schema: Schema): Future[UUID] = run(schemas.insert(lift(schema)).returning(_.id))
  def all()                                = run(schemas)

  val schemaVersions = quote(querySchema[SchemaVersion]("schema_versions"))

  def createVersion(schemaVersion: SchemaVersion): Future[UUID] =
    run(schemaVersions.insert(lift(schemaVersion)).returning(_.id))

  def findFirstVersions(filter: PaginatedFilter) = {
    val query = quote {
      applyCursors(lift(filter)).sortBy(_.createdOn)(Ord.descNullsLast).take(lift(filter.take))
    }

    run(query)
  }

  def findLastVersions(filter: PaginatedFilter) = {
    val query = quote {
      applyCursors(lift(filter)).sortBy(_.createdOn)(Ord.ascNullsLast).take(lift(filter.take))
    }

    run(query)
  }

  private def applyCursors =
    quote { (filter: PaginatedFilter) =>
      schemaVersions
        .filter(
          (version: SchemaVersion) =>
            filter.id.forall(_ == version.schemaId)
              && filter.after > version.createdOn
              && filter.before < version.createdOn
        )
    }

  def findLatestVersion(id: UUID) = {
    val query = quote {
      schemaVersions
        .filter(_.schemaId == lift(id))
        .filter { v1 =>
          schemaVersions
            .filter(_.schemaId == lift(id))
            .filter { v2 =>
              v1.id != v2.id && v1.createdOn < v2.createdOn
            }
            .isEmpty
        }
    }

    run(query).map(_.headOption)
  }

  def findVersion(id: UUID, version: String) = {
    val query = quote {
      schemaVersions.filter(_.version == lift(version)).filter(_.schemaId == lift(id))
    }

    run(query).map(_.headOption)
  }
}
