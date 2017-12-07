package schemer.registry.models

import java.util.UUID

import org.joda.time.DateTime

case class Schema(id: UUID, name: String, namespace: String, `type`: String, createdOn: DateTime, createdBy: String)

object Schema {
  def apply(name: String, namespace: String, `type`: String, createdOn: DateTime, createdBy: String) =
    new Schema(UUID.randomUUID(), name, namespace, `type`, createdOn, createdBy)
}
