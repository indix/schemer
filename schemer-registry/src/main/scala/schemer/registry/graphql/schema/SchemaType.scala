package schemer.registry.graphql.schema

import sangria.macros.derive.deriveObjectType
import sangria.schema.{Field, ObjectType, _}
import schemer.registry.graphql.{SchemaVersionLatestDeferred, SchemaVersionsDeferred}
import schemer.registry.graphql.schema.SchemaDefinition.constantComplexity
import schemer.registry.models.{
  PageInfo,
  SchemaSchemaVersionConnection,
  SchemaSchemaVersionEdge,
  SchemaVersion,
  Schema => SSchema,
  SchemaType => SSchemaType
}

trait SchemaType extends GraphQLCustomTypes {
  lazy implicit val SchemaTypeType = EnumType[SSchemaType](
    "SchemaType",
    Some("Supported schema types"),
    List(
      EnumValue("Avro", value = SSchemaType.Avro),
      EnumValue("Csv", value = SSchemaType.Csv),
      EnumValue("Json", value = SSchemaType.Json),
      EnumValue("Parquet", value = SSchemaType.Parquet)
    )
  )
  lazy implicit val FirstArg                                                               = Argument("first", OptionInputType(IntType))
  lazy implicit val AfterArg                                                               = Argument("after", OptionInputType(StringType))
  lazy implicit val LastArg                                                                = Argument("last", OptionInputType(IntType))
  lazy implicit val BeforeArg                                                              = Argument("before", OptionInputType(StringType))
  lazy implicit val PageInfo: ObjectType[Unit, PageInfo]                                   = deriveObjectType()
  lazy implicit val SchemaVersionType: ObjectType[Unit, SchemaVersion]                     = deriveObjectType()
  lazy implicit val SchemaSchemaVersionEdgeType: ObjectType[Unit, SchemaSchemaVersionEdge] = deriveObjectType()
  lazy implicit val SchemaSchemaVersionConnectionType: ObjectType[Unit, SchemaSchemaVersionConnection] =
    deriveObjectType()

  val SchemaType: ObjectType[Unit, SSchema] = ObjectType(
    "Schema",
    "Schema",
    fields[Unit, SSchema](
      Field(
        "id",
        UUIDType,
        resolve = _.value.id
      ),
      Field(
        "name",
        StringType,
        resolve = _.value.name
      ),
      Field(
        "namespace",
        StringType,
        resolve = _.value.namespace
      ),
      Field(
        "type",
        SchemaTypeType,
        resolve = ctx => SSchemaType.supportedTypes.find(_.`type` == ctx.value.`type`).get
      ),
      Field(
        "createdOn",
        DateTimeType,
        resolve = _.value.createdOn
      ),
      Field(
        "createdBy",
        StringType,
        resolve = _.value.createdBy
      ),
      Field(
        "versions",
        ListType(SchemaSchemaVersionConnectionType),
        resolve = ctx =>
          SchemaVersionsDeferred(ctx.value.id, ctx arg FirstArg, ctx arg AfterArg, ctx arg LastArg, ctx arg BeforeArg),
        complexity = constantComplexity(200),
        arguments = List(FirstArg, AfterArg, LastArg, BeforeArg)
      ),
      Field(
        "latestVersion",
        OptionType(SchemaVersionType),
        resolve = ctx => SchemaVersionLatestDeferred(ctx.value.id),
        complexity = constantComplexity(200)
      )
    )
  )

}
