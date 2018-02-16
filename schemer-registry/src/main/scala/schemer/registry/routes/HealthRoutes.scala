package schemer.registry.routes

import java.io.{StringWriter, Writer}
import java.util

import akka.http.scaladsl.model.{HttpCharsets, HttpEntity, MediaType}
import akka.http.scaladsl.server.Directives._
import io.prometheus.client.Collector.MetricFamilySamples
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.hotspot.{DefaultExports, StandardExports}

trait HealthRoutes {

  DefaultExports.initialize()
  val collectorRegistry       = CollectorRegistry.defaultRegistry
  private val mediaTypeParams = Map("version" -> "0.0.4")
  private val mediaType =
    MediaType.customWithFixedCharset("text", "plain", HttpCharsets.`UTF-8`, params = mediaTypeParams)

  def toPrometheusTextFormat(e: util.Enumeration[MetricFamilySamples]): String = {
    val writer: Writer = new StringWriter()
    TextFormat.write004(writer, e)

    writer.toString
  }

  val healthRoutes = path("health") {
    get {
      complete {
        "OK"
      }
    }
  } ~ path("metrics") {
    get {
      complete {
        HttpEntity(mediaType, toPrometheusTextFormat(collectorRegistry.metricFamilySamples()))
      }
    }
  }
}
