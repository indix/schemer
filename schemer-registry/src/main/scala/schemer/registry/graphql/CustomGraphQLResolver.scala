package schemer.registry.graphql

import sangria.execution.deferred.{Deferred, DeferredResolver}
import schemer.{CSVOptions, CSVSchema, JSONSchema, ParquetSchema}

import scala.concurrent.ExecutionContext

case class InferCSVSchemaDeferred(options: CSVOptions, paths: Seq[String]) extends Deferred[CSVSchema]
case class InferJSONSchemaDeferred(paths: Seq[String])                     extends Deferred[JSONSchema]
case class InferParquetSchemaDeferred(`type`: String, paths: Seq[String])  extends Deferred[ParquetSchema]

class CustomGraphQLResolver extends DeferredResolver[GraphQLService] {
  override def resolve(deferred: Vector[Deferred[Any]], ctx: GraphQLService, queryState: Any)(
      implicit ec: ExecutionContext
  ) = {
    val defMap = deferred.collect {
      case InferCSVSchemaDeferred(options, paths) => "csvSchemaInference"     -> ctx.inferCSVSchema(options, paths)
      case InferJSONSchemaDeferred(paths)         => "jsonSchemaInference"    -> ctx.inferJSONSchema(paths)
      case InferParquetSchemaDeferred(t, paths)   => "parquetSchemaInference" -> ctx.inferParquetSchema(t, paths)
    }

    deferred flatMap {
      case InferCSVSchemaDeferred(_, _)     => defMap.filter(_._1 == "csvSchemaInference").map(_._2)
      case InferJSONSchemaDeferred(_)       => defMap.filter(_._1 == "jsonSchemaInference").map(_._2)
      case InferParquetSchemaDeferred(_, _) => defMap.filter(_._1 == "parquetSchemaInference").map(_._2)
    }
  }
}

object CustomGraphQLResolver {
  val deferredResolver: DeferredResolver[GraphQLService] =
    DeferredResolver.fetchersWithFallback(
      new CustomGraphQLResolver
    )
}
