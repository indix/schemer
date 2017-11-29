package schemer.registry.graphql

import sangria.execution.deferred.{Deferred, DeferredResolver}
import schemer.{CSVOptions, CSVSchema}

import scala.concurrent.ExecutionContext

case class InferCSVSchemaDeferred(options: CSVOptions, paths: Seq[String]) extends Deferred[CSVSchema]

class CustomGraphQLResolver extends DeferredResolver[GraphQLService] {
  override def resolve(deferred: Vector[Deferred[Any]], ctx: GraphQLService, queryState: Any)(
      implicit ec: ExecutionContext
  ) = {
    val defMap = deferred.collect {
      case InferCSVSchemaDeferred(options, paths) => "csvSchemaInference" -> ctx.inferCSVSchema(options, paths)
    }

    deferred flatMap {
      case InferCSVSchemaDeferred(_, _) => defMap.filter(_._1 == "csvSchemaInference").map(_._2)
    }
  }
}

object CustomGraphQLResolver {
  val deferredResolver: DeferredResolver[GraphQLService] =
    DeferredResolver.fetchersWithFallback(
      new CustomGraphQLResolver
    )
}
