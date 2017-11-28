package schemer.registry.routes

import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK}
import akka.http.scaladsl.server.Directives.{as, complete, entity, get, getFromResource, path, post}
import sangria.execution._
import sangria.parser.QueryParser
import sangria.schema.Schema
import spray.json.{JsObject, JsString, JsValue}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.server.Directives._
import sangria.marshalling.sprayJson._
import schemer.registry.graphql.{CustomGraphQLResolver, GraphQLService}
import schemer.registry.graphql.schema.SchemaDefinition

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

trait GraphQLRoutes {
  val graphQLService: GraphQLService

  case object TooComplexQuery extends Exception
  val rejectComplexQueries = QueryReducer.rejectComplexQueries(
    300,
    (_: Double, _: GraphQLService) => TooComplexQuery
  )

  val graphQLExceptionHandler: Executor.ExceptionHandler = {
    case (_, TooComplexQuery) => HandledException("Too complex query. Please reduce the field selection.")
  }

  def executeGraphQLQuery(schema: Schema[GraphQLService, Unit], requestJson: JsValue) = {
    val JsObject(fields) = requestJson

    val JsString(query) = fields("query")

    val operation = fields.get("operationName") collect {
      case JsString(op) => op
    }

    val vars = fields.get("variables") match {
      case Some(obj: JsObject) => obj
      case _                   => JsObject.empty
    }

    QueryParser.parse(query) match {

      case Success(queryDocument) =>
        complete(
          Executor
            .execute(
              schema,
              queryDocument,
              graphQLService,
              deferredResolver = CustomGraphQLResolver.deferredResolver,
              variables = vars,
              operationName = operation,
              queryReducers = rejectComplexQueries :: Nil,
              exceptionHandler = graphQLExceptionHandler
            )
            .map(OK -> _)
            .recover {
              case error: QueryAnalysisError => BadRequest          -> error.resolveError
              case error: ErrorWithResolver  => InternalServerError -> error.resolveError
            }
        )

      case Failure(error) =>
        complete(BadRequest -> JsObject("error" -> JsString(error.getMessage)))
    }
  }

  val graphQLRoutes = path("graphql") {
    post {
      entity(as[JsValue]) { requestJson =>
        executeGraphQLQuery(SchemaDefinition.schema, requestJson)
      }
    } ~ get {
      getFromResource("graphql/graphiql.html")
    }
  }
}
