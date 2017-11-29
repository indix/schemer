package schemer.registry.graphql.schema

import buildinfo.BuildInfo
import sangria.macros.derive.deriveObjectType
import sangria.schema.ObjectType

case class Metadata(version: String = BuildInfo.version)

trait MetadataType {
  lazy val MetadataType: ObjectType[Unit, Metadata] = deriveObjectType()
}
