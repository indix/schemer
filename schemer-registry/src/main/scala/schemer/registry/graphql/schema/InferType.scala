package schemer.registry.graphql.schema

import sangria.schema._
import schemer.CSVOptions
import schemer.registry.graphql.schema.SchemaDefinition.constantComplexity
import sangria.macros.derive.deriveInputObjectType
import schemer.registry.graphql.{GraphQLService, InferCSVSchemaDeferred}
import spray.json.DefaultJsonProtocol
import sangria.marshalling.sprayJson._

case class GCSVSchema(schema: String)

trait InferType extends DefaultJsonProtocol {
  lazy implicit val PathsArg                                         = Argument("paths", ListInputType(StringType))
  implicit val CSVOptionsFormat                                      = jsonFormat5(CSVOptions.apply)
  lazy implicit val CSVOptionsInputType: InputObjectType[CSVOptions] = deriveInputObjectType[CSVOptions]()
  lazy implicit val CSVOptionsArg                                    = Argument("csvOptions", OptionInputType(CSVOptionsInputType), CSVOptions())

  lazy val CSVSchemaType = ObjectType(
    "CSVSchema",
    "CSV Schema",
    fields[Unit, GCSVSchema](
      Field(
        "json",
        StringType,
        description = Some("CSV Schema as json"),
        complexity = constantComplexity(100),
        resolve = _.value.schema
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
        description = Some("CSV Schema inference from options and paths"),
        complexity = constantComplexity(100),
        resolve = ctx => InferCSVSchemaDeferred(ctx arg CSVOptionsArg, ctx arg PathsArg),
        arguments = List(CSVOptionsArg, PathsArg)
      )
    )
  )
}
