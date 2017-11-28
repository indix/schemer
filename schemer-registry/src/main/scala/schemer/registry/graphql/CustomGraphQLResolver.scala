package schemer.registry.graphql

import sangria.execution.deferred.{Deferred, DeferredResolver}

import scala.concurrent.{ExecutionContext, Future}

class CustomGraphQLResolver extends DeferredResolver[GraphQLService] {
  override def resolve(deferred: Vector[Deferred[Any]], ctx: GraphQLService, queryState: Any)(
      implicit ec: ExecutionContext
  ) =
    Vector.empty[Future[Any]]
}

object CustomGraphQLResolver {
  val deferredResolver: DeferredResolver[GraphQLService] =
    DeferredResolver.fetchersWithFallback(
      new CustomGraphQLResolver
    )
}
