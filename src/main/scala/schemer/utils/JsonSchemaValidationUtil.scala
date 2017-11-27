package schemer.utils

import com.github.fge.jsonschema.core.report.ProcessingReport

object JsonSchemaValidationUtil {
  def process(report: ProcessingReport): List[String] =
    if (!report.isSuccess) {
      getErrorsFromReport(report)
    } else {
      List.empty
    }

  private def getErrorsFromReport(report: ProcessingReport) = {
    val errorList = report.iterator.asScala.toList
      .map { message =>
        message.asJson()
      }
      .filter { json =>
        json.get("level").asText == "error"
      }
      .map { json =>
        json.get("message").asText
      }
    errorList
  }
}
