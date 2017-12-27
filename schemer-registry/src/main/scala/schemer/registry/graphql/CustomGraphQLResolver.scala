package schemer.registry.graphql

import java.util.UUID

import sangria.execution.deferred.{Deferred, DeferredResolver}
import schemer._
import schemer.registry.models.SchemaVersion

import scala.concurrent.ExecutionContext

case class InferCSVSchemaDeferred(options: CSVOptions, paths: Seq[String]) extends Deferred[CSVSchema]
case class InferJSONSchemaDeferred(paths: Seq[String])                     extends Deferred[JSONSchema]
case class InferParquetSchemaDeferred(`type`: String, paths: Seq[String])  extends Deferred[ParquetSchema]
case class InferAvroSchemaDeferred(paths: Seq[String])                     extends Deferred[AvroSchema]

case class SchemaVersionsDeferred(id: UUID)      extends Deferred[Seq[SchemaVersion]]
case class SchemaVersionLatestDeferred(id: UUID) extends Deferred[Option[SchemaVersion]]

class CustomGraphQLResolver extends DeferredResolver[GraphQLService] {
  override def resolve(deferred: Vector[Deferred[Any]], ctx: GraphQLService, queryState: Any)(
      implicit ec: ExecutionContext
  ) = {
    val defMap = deferred.collect {
      case InferCSVSchemaDeferred(options, paths) => "csvSchemaInference"     -> ctx.inferCSVSchema(options, paths)
      case InferJSONSchemaDeferred(paths)         => "jsonSchemaInference"    -> ctx.inferJSONSchema(paths)
      case InferParquetSchemaDeferred(t, paths)   => "parquetSchemaInference" -> ctx.inferParquetSchema(t, paths)
      case InferAvroSchemaDeferred(paths)         => "avroSchemaInference"    -> ctx.inferAvroSchema(paths)
      case SchemaVersionsDeferred(id)             => "schemaVersions"         -> ctx.schemaVersions(id)
      case SchemaVersionLatestDeferred(id)        => "schemaVersionLatest"    -> ctx.latestSchemaVersion(id)
    }

    deferred flatMap {
      case InferCSVSchemaDeferred(_, _)     => defMap.filter(_._1 == "csvSchemaInference").map(_._2)
      case InferJSONSchemaDeferred(_)       => defMap.filter(_._1 == "jsonSchemaInference").map(_._2)
      case InferParquetSchemaDeferred(_, _) => defMap.filter(_._1 == "parquetSchemaInference").map(_._2)
      case InferAvroSchemaDeferred(_)       => defMap.filter(_._1 == "avroSchemaInference").map(_._2)
      case SchemaVersionsDeferred(_)        => defMap.filter(_._1 == "schemaVersions").map(_._2)
      case SchemaVersionLatestDeferred(_)   => defMap.filter(_._1 == "schemaVersionLatest").map(_._2)
    }
  }
}

object CustomGraphQLResolver {
  val deferredResolver: DeferredResolver[GraphQLService] =
    DeferredResolver.fetchersWithFallback(
      new CustomGraphQLResolver
    )
}
