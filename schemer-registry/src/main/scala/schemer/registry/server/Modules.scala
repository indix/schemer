package schemer.registry.server

import akka.actor.{ActorSystem, Props}
import akka.routing.BalancingPool
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

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

trait Modules {

  implicit def system: ActorSystem

  implicit def ec: ExecutionContext

  implicit def mat: Materializer

  lazy val config = new ServerConfig with DatabaseConfig with InferenceConfig {
    override def rootConfig: Config = loadDefault("registry")
  }

  implicit lazy val clock = RealTimeClock

  implicit val spark: SparkSession = SparkSession.builder
    .config(new SparkConf())
    .master("local[*]")
    .getOrCreate()

  val hadoopConf = spark.sparkContext.hadoopConfiguration
  hadoopConf.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")

  val sqlDatabase = SqlDatabase(config)
  sqlDatabase.updateSchema()

  lazy val schemaDao = new SchemaDao(sqlDatabase)
  lazy val inferActor = locally {
    implicit lazy val inferTimeout = Timeout(config.inferTimeout)
    system.actorOf(Props(new InferActor()).withRouter(BalancingPool(nrOfInstances = 10)), name = "InferActor")
  }
  lazy val graphQLService = locally {
    implicit lazy val inferActorTimeout = Timeout(config.inferTimeout + 20.seconds)
    new GraphQLService(schemaDao, inferActor)
  }
}
