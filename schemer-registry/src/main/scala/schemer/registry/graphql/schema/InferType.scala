package schemer.registry.graphql.schema

import sangria.schema._
import schemer._
import schemer.registry.graphql.schema.SchemaDefinition.constantComplexity
import sangria.macros.derive.{deriveInputObjectType, deriveObjectType, InputObjectTypeName}
import schemer.registry.graphql.{
  GraphQLService,
  InferCSVSchemaDeferred,
  InferJSONSchemaDeferred,
  InferParquetSchemaDeferred
}
import spray.json.DefaultJsonProtocol
import sangria.marshalling.sprayJson._

trait InferType extends DefaultJsonProtocol {
  lazy implicit val TypeArg             = Argument("type", StringType)
  lazy implicit val PathsArg            = Argument("paths", ListInputType(StringType))
  implicit val CSVOptionsFormat         = jsonFormat5(CSVOptions.apply)
  lazy implicit val CSVOptionsInputType = deriveInputObjectType[CSVOptions](InputObjectTypeName("CSVOptionsInput"))
  lazy implicit val CSVOptionsArg       = Argument("csvOptions", OptionInputType(CSVOptionsInputType), CSVOptions())

  lazy implicit val CSVFieldType   = deriveObjectType[Unit, CSVField]()
  lazy implicit val CSVOptionsType = deriveObjectType[Unit, CSVOptions]()
  lazy val CSVSchemaType = ObjectType(
    "CSVSchema",
    "CSV Schema",
    fields[Unit, CSVSchema](
      Field(
        "fields",
        ListType(CSVFieldType),
        description = Some("Fields of the CSV Schema"),
        complexity = constantComplexity(1),
        resolve = ctx => ctx.value.fields
      ),
      Field(
        "options",
        CSVOptionsType,
        description = Some("Options of the CSV Schema"),
        complexity = constantComplexity(1),
        resolve = ctx => ctx.value.options
      ),
      Field(
        "schema",
        StringType,
        description = Some("CSV Schema as JSON string"),
        complexity = constantComplexity(100),
        resolve = ctx => ctx.value.schema()
      ),
      Field(
        "sparkSchema",
        StringType,
        description = Some("Spark Schema as JSON string"),
        complexity = constantComplexity(100),
        resolve = ctx => ctx.value.sparkSchema().prettyJson
      )
    )
  )

  lazy val JSONSchemaType = ObjectType(
    "JSONSchema",
    "JSON Schema",
    fields[Unit, JSONSchema](
      Field(
        "schema",
        StringType,
        description = Some("CSV Schema as JSON string"),
        complexity = constantComplexity(10),
        resolve = ctx => ctx.value.schema
      ),
      Field(
        "sparkSchema",
        StringType,
        description = Some("Spark Schema as JSON string"),
        complexity = constantComplexity(100),
        resolve = ctx => ctx.value.sparkSchema().prettyJson
      )
    )
  )

  lazy val ParquetSchemaType = ObjectType(
    "ParquetSchema",
    "Parquet Schema",
    fields[Unit, ParquetSchema](
      Field(
        "type",
        StringType,
        description = Some("Parquet Schema type"),
        complexity = constantComplexity(10),
        resolve = ctx => ctx.value.`type`
      ),
      Field(
        "schema",
        StringType,
        description = Some("Parquet Schema as JSON string"),
        complexity = constantComplexity(10),
        resolve = ctx => ctx.value.schema
      ),
      Field(
        "sparkSchema",
        StringType,
        description = Some("Spark Schema as JSON string"),
        complexity = constantComplexity(100),
        resolve = ctx => ctx.value.sparkSchema().prettyJson
      )
    )
  )

  lazy val InferType = ObjectType(
    "Inference",
    "Schema Inference",
    fields[GraphQLService, Unit](
      Field(
        "csv",
        CSVSchemaType,
        description = Some("CSV Schema inference"),
        complexity = constantComplexity(500),
        resolve = ctx => InferCSVSchemaDeferred(ctx arg CSVOptionsArg, ctx arg PathsArg),
        arguments = List(CSVOptionsArg, PathsArg)
      ),
      Field(
        "json",
        JSONSchemaType,
        description = Some("JSON Schema inference"),
        complexity = constantComplexity(500),
        resolve = ctx => InferJSONSchemaDeferred(ctx arg PathsArg),
        arguments = List(PathsArg)
      ),
      Field(
        "parquet",
        ParquetSchemaType,
        description = Some("Parquet Schema inference"),
        complexity = constantComplexity(500),
        resolve = ctx => InferParquetSchemaDeferred(ctx arg TypeArg, ctx arg PathsArg),
        arguments = List(TypeArg, PathsArg)
      )
    )
  )
}
