package schemer.registry.exceptions

case class SchemerVersionCreationException(message: String)
    extends SchemerException(s"Error while trying to create new schema version - $message")
