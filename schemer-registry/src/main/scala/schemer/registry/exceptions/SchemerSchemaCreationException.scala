package schemer.registry.exceptions

case class SchemerSchemaCreationException(message: String)
    extends SchemerException(s"Error while trying to create new schema - $message")
