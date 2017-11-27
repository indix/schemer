package schemer.registry.utils

import org.joda.time.{DateTime, DateTimeZone, Duration}
import org.joda.time.format.PeriodFormatterBuilder

trait Clock {
  def now: DateTime
  def nowUtc: DateTime
  def nowMillis: Long
}

object RealTimeClock extends Clock with Serializable {
  def now       = DateTime.now()
  def nowUtc    = DateTime.now(DateTimeZone.UTC)
  def nowMillis = System.currentTimeMillis()
}

class FixtureTimeClock(millis: Long) extends Clock with Serializable {
  def now       = new DateTime(millis)
  def nowUtc    = new DateTime(millis, DateTimeZone.UTC)
  def nowMillis = millis
}

class FormatDuration() {
  def format(time: Duration): String = {
    val period = time.toPeriod()
    val hms = new PeriodFormatterBuilder()
      .printZeroAlways()
      .appendHours()
      .appendSeparator(" hours ")
      .appendMinutes()
      .appendSeparator(" minutes ")
      .appendSeconds()
      .appendSuffix(" seconds")
      .toFormatter()
    hms.print(period)
  }
}
