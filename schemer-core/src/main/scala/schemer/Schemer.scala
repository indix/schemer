package schemer

sealed trait SchemaType {
  val `type`: String
}

object SchemaType {
  case object Avro extends SchemaType {
    override val `type`: String = "avro"
  }
  case object Csv extends SchemaType {
    override val `type`: String = "csv"
  }
  case object Json extends SchemaType {
    override val `type`: String = "json"
  }
  case object ParquetAvro extends SchemaType {
    override val `type`: String = "parquet_avro"
  }
  case object ParquetCsv extends SchemaType {
    override val `type`: String = "parquet_csv"
  }
  case object ParquetJson extends SchemaType {
    override val `type`: String = "parquet_json"
  }
  val supportedTypes = List(Avro, Csv, Json, ParquetAvro, ParquetCsv, ParquetJson)
}

object Schemer {
  def from(`type`: String, config: String): SchemaLike = `type` match {
    case "avro"         => AvroSchema(config)
    case "csv"          => CSVSchema(config)
    case "json"         => JSONSchema(config)
    case "parquet_avro" => ParquetSchema(config, ParquetSchemaType.Avro)
    case "parquet_csv"  => ParquetSchema(config, ParquetSchemaType.Csv)
    case "parquet_json" => ParquetSchema(config, ParquetSchemaType.Json)
  }
}
