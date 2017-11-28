package schemer.registry.server

trait ServerConfig extends ConfigWithDefault {

  def rootConfig: Config

  lazy val serverHost: String = rootConfig.getString("server.host")
  lazy val serverPort: Int    = rootConfig.getInt("server.port")
}
