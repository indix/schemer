package schemer.registry.server

import akka.actor.ActorSystem
import akka.stream.Materializer
import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.StrictLogging
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import schemer.registry.graphql.GraphQLService
import schemer.registry.utils.RealTimeClock

import scala.concurrent.ExecutionContext

trait Modules extends StrictLogging {

  implicit def system: ActorSystem

  implicit def ec: ExecutionContext

  implicit def mat: Materializer

  lazy val config = new ServerConfig {
    override def rootConfig: Config = loadDefault("registry")
  }

  implicit lazy val clock = RealTimeClock

  implicit val spark: SparkSession = SparkSession.builder
    .config(new SparkConf())
    .master("local[*]")
    .getOrCreate()

  val hadoopConf = spark.sparkContext.hadoopConfiguration
  hadoopConf.set("fs.s3.impl", "org.apache.hadoop.fs.s3native.NativeS3FileSystem")

  lazy val graphQLService = new GraphQLService()
}
