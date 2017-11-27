package schemer

import org.apache.spark.sql.types.StructType
import org.apache.spark.sql.{DataFrame, SparkSession}

private[schemer] trait SchemaLikeBase[T <: SchemaLike] {
  val options: Map[String, String] = Map()
  def infer(paths: String*)(implicit spark: SparkSession): T
}

private[schemer] trait SchemaLike {
  def validate: List[String]

  def sparkSchema(): StructType

  def schema(): String

  def toDf(paths: String*)(implicit spark: SparkSession): DataFrame
}
