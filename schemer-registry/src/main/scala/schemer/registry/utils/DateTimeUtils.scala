package schemer.registry.utils

import java.nio.charset.StandardCharsets
import java.util.Base64

import org.joda.time.DateTime
import schemer.registry.Cursor

object DateTimeUtils {
  implicit class DateTimeCursor(val dt: DateTime) {
    def toCursor: Cursor = Base64.getEncoder.encodeToString(dt.getMillis.toString.getBytes(StandardCharsets.UTF_8))
  }

  implicit class CursorDateTime(val cursor: Cursor) {
    def toDateTime: DateTime =
      new DateTime(new String(Base64.getDecoder.decode(cursor), StandardCharsets.UTF_8).toLong)
  }

}
