package schemer.registry.graphql.schema

import sangria.schema.{fields, Args, Field, ListType, ObjectType, OptionType, Schema}
import schemer.registry.graphql.GraphQLService
import schemer.registry.models.{Schema => SSchema}

object SchemaDefinition extends InferType with MetadataType with MutationType with SchemaType with GraphQLCustomTypes {

  def constantComplexity[Ctx](complexity: Double) =
    Some((_: Ctx, _: Args, child: Double) => child + complexity)

  val QueryType = ObjectType(
    "Query",
    "Root",
    fields[GraphQLService, Unit](
      Field(
        "schema",
        OptionType(SchemaType),
        description = Some("Schema"),
        resolve = ctx => ctx.ctx.schema(ctx arg IdArg),
        arguments = List(IdArg)
      ),
      Field(
        "schemas",
        ListType(SchemaType),
        description = Some("All Schemas"),
        resolve = ctx => ctx.ctx.allSchemas
      ),
      Field(
        "infer",
        InferType,
        description = Some("Schema Inference"),
        resolve = _ => ()
      ),
      Field(
        "metadata",
        MetadataType,
        description = Some("Metadata"),
        complexity = constantComplexity(100),
        resolve = _ => Metadata()
      )
    )
  )
  val schema = Schema(QueryType, Some(MutationType))
}
