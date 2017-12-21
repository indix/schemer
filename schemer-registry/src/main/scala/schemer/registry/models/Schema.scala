package schemer.registry.models

import java.util.UUID

import org.joda.time.DateTime

sealed trait SchemaType {
  val `type`: String
}

object SchemaType {
  case object Avro extends SchemaType {
    override val `type`: String = "avro"
  }
  case object Csv extends SchemaType {
    override val `type`: String = "csv"
  }
  case object Json extends SchemaType {
    override val `type`: String = "json"
  }
  case object Parquet extends SchemaType {
    override val `type`: String = "parquet"
  }

  val supportedTypes = List(Avro, Csv, Json, Parquet)
}

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
