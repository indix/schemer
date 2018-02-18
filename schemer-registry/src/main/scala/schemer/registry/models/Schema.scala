package schemer.registry.models

import java.util.UUID

import org.joda.time.DateTime

case class Schema(
    id: UUID,
    name: String,
    namespace: String,
    `type`: String,
    createdOn: DateTime,
    createdBy: String
)

object Schema {
  def apply(name: String, namespace: String, `type`: String, createdOn: DateTime, createdBy: String) =
    new Schema(null, name, namespace, `type`, createdOn, createdBy)
}

case class SchemaVersion(
    id: UUID,
    schemaId: UUID,
    version: String,
    schema: String,
    createdOn: DateTime,
    createdBy: String
)
case class PageInfo(hasNextPage: Boolean, hasPreviousPage: Boolean) {
  def hasMore = hasNextPage || hasPreviousPage
}
case class SchemaSchemaVersionEdge(cursor: String, node: SchemaVersion)
case class SchemaSchemaVersionConnection(pageInfo: PageInfo, edges: List[SchemaSchemaVersionEdge])
