package schemer.registry.exceptions

case class SchemerInferenceException(message: String)
    extends SchemerException(s"Error while trying to infer schema - $message")
