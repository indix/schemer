package schemer.registry.graphql.schema

import sangria.macros.derive.deriveContextObjectType
import sangria.schema.{EnumType, EnumValue}
import schemer.registry.graphql.GraphQLService
import schemer.registry.models.SchemaType

trait MutationType extends JSONSchemaType with GraphQLCustomTypes {
  implicit lazy val SchemaTypeType = EnumType[SchemaType](
    "SchemaType",
    Some("Supported schema types"),
    List(
      EnumValue("Avro", value = SchemaType.Avro),
      EnumValue("Csv", value = SchemaType.Csv),
      EnumValue("Json", value = SchemaType.Json),
      EnumValue("Parquet", value = SchemaType.Parquet)
    )
  )

  val MutationType = deriveContextObjectType[GraphQLService, GraphQLService, Unit](identity)
}
