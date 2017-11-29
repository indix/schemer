package schemer.registry.graphql.schema

import buildinfo.BuildInfo
import sangria.macros.derive.deriveObjectType
import sangria.schema.{fields, Args, Field, ObjectType, Schema}
import schemer.registry.graphql.GraphQLService

case class Metadata(version: String = BuildInfo.version)

trait MetadataType {
  lazy val MetadataType: ObjectType[Unit, Metadata] = deriveObjectType()
}

object SchemaDefinition extends MetadataType with GraphQLCustomTypes {

  def constantComplexity[Ctx](complexity: Double) =
    Some((_: Ctx, _: Args, child: Double) => child + complexity)

  val QueryType = ObjectType(
    "Query",
    "Root",
    fields[GraphQLService, Unit](
      Field(
        "metadata",
        MetadataType,
        description = Some("Metadata"),
        complexity = constantComplexity(100),
        resolve = _ => Metadata()
      )
    )
  )
  val schema = Schema(QueryType)
}
