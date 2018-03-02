package schemer.registry.routes

import java.io.{StringWriter, Writer}
import java.util

import akka.http.scaladsl.model.{HttpCharsets, HttpEntity, MediaType}
import akka.http.scaladsl.server.Directives._
import io.prometheus.client.Collector.MetricFamilySamples
import io.prometheus.client.CollectorRegistry
import io.prometheus.client.exporter.common.TextFormat
import io.prometheus.client.hotspot.DefaultExports

trait HealthRoutes {

  DefaultExports.initialize()
  private val collectorRegistry      = CollectorRegistry.defaultRegistry
  private val metricsMediaTypeParams = Map("version" -> "0.0.4")
  private val metricsMediaType =
    MediaType.customWithFixedCharset("text", "plain", HttpCharsets.`UTF-8`, params = metricsMediaTypeParams)

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
        HttpEntity(metricsMediaType, toPrometheusTextFormat(collectorRegistry.metricFamilySamples()))
      }
    }
  }
}
