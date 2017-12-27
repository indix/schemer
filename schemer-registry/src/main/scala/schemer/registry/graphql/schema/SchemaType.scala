package schemer.registry.graphql.schema

import sangria.macros.derive.deriveObjectType
import sangria.schema.{Field, ObjectType, _}
import schemer.registry.graphql.{SchemaVersionLatestDeferred, SchemaVersionsDeferred}
import schemer.registry.graphql.schema.SchemaDefinition.constantComplexity
import schemer.registry.models.{SchemaVersion, Schema => SSchema, SchemaType => SSchemaType}

trait SchemaType extends GraphQLCustomTypes {
  implicit lazy val SchemaTypeType = EnumType[SSchemaType](
    "SchemaType",
    Some("Supported schema types"),
    List(
      EnumValue("Avro", value = SSchemaType.Avro),
      EnumValue("Csv", value = SSchemaType.Csv),
      EnumValue("Json", value = SSchemaType.Json),
      EnumValue("Parquet", value = SSchemaType.Parquet)
    )
  )
  val SchemaVersionType: ObjectType[Unit, SchemaVersion] = deriveObjectType()
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
        ListType(SchemaVersionType),
        resolve = ctx => SchemaVersionsDeferred(ctx.value.id),
        complexity = constantComplexity(200)
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
