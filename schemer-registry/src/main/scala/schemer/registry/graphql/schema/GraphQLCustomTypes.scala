package schemer.registry.graphql.schema

import java.util.UUID

import org.joda.time.format.ISODateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}
import sangria.ast
import sangria.schema.ScalarType
import sangria.validation.ValueCoercionViolation

import scala.util.{Failure, Success, Try}

trait GraphQLCustomTypes {
  case object DateCoercionViolation extends ValueCoercionViolation("Date value expected")

  def parseDate(s: String) = Try(new DateTime(s, DateTimeZone.UTC)) match {
    case Success(date) => Right(date)
    case Failure(_)    => Left(DateCoercionViolation)
  }

  def parseUUID(s: String) = Try(UUID.fromString(s)) match {
    case Success(uuid) => Right(uuid)
    case Failure(_)    => Left(DateCoercionViolation)
  }

  implicit val DateTimeType = ScalarType[DateTime](
    "DateTime",
    coerceOutput = (date: DateTime, _) => ast.StringValue(ISODateTimeFormat.dateTime().print(date)),
    coerceUserInput = {
      case s: String => parseDate(s)
      case _         => Left(DateCoercionViolation)
    },
    coerceInput = {
      case ast.StringValue(s, _, _) => parseDate(s)
      case _                        => Left(DateCoercionViolation)
    }
  )

  implicit val UUIDType = ScalarType[UUID](
    "UUID",
    coerceOutput = (uuid: UUID, _) => ast.StringValue(uuid.toString),
    coerceUserInput = {
      case s: String => parseUUID(s)
      case _         => Left(DateCoercionViolation)
    },
    coerceInput = {
      case ast.StringValue(s, _, _) => parseUUID(s)
      case _                        => Left(DateCoercionViolation)
    }
  )
}
