package schemer.registry.graphql.schema

import sangria.schema.{Field, ObjectType, _}
import schemer.registry.models.{Schema => SSchema, SchemaType => SSchemaType}

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
      )
    )
  )

}
