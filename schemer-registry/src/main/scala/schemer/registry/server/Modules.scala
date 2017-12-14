package schemer.registry.server

import akka.actor.{ActorSystem, Props}
import akka.routing.{BalancingPool, RoundRobinPool}
import akka.stream.Materializer
import akka.util.Timeout
import com.typesafe.config.Config
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import schemer.registry.actors.InferActor
import schemer.registry.dao.SchemaDao
import schemer.registry.graphql.GraphQLService
import schemer.registry.sql.{DatabaseConfig, SqlDatabase}
import schemer.registry.utils.RealTimeClock

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext

trait Modules {

  implicit def system: ActorSystem

  implicit def ec: ExecutionContext

  implicit def mat: Materializer

  lazy val config = new ServerConfig with DatabaseConfig {
    override def rootConfig: Config = loadDefault("registry")
  }

  implicit lazy val clock = RealTimeClock

  implicit lazy val inferTimeout = Timeout(60.seconds)

  implicit val spark: SparkSession = SparkSession.builder
    .config(new SparkConf())
    .master("local[*]")
    .getOrCreate()

  val hadoopConf = spark.sparkContext.hadoopConfiguration
  hadoopConf.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")

  val sqlDatabase = SqlDatabase(config)
  sqlDatabase.updateSchema()

  lazy val schemaDao = new SchemaDao(sqlDatabase)

  lazy val inferActor =
    system.actorOf(Props(new InferActor()).withRouter(BalancingPool(nrOfInstances = 10)), name = "InferActor")
  lazy val graphQLService = new GraphQLService(schemaDao, inferActor)
}
