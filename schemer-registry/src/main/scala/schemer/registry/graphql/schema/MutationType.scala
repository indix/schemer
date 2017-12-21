package schemer.registry.graphql.schema

import sangria.macros.derive.deriveContextObjectType
import schemer.registry.graphql.GraphQLService

trait MutationType extends JSONSchemaType with SchemaType with GraphQLCustomTypes {
  val MutationType = deriveContextObjectType[GraphQLService, GraphQLService, Unit](identity)
}
