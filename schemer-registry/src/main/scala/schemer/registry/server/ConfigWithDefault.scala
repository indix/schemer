package schemer.registry.server

import java.net.InetAddress
import java.util.concurrent.TimeUnit

import com.typesafe.config.{Config, ConfigFactory}

trait ConfigWithDefault {

  def rootConfig: Config

  def getBoolean(path: String, default: Boolean) = ifHasPath(path, default) { _.getBoolean(path) }
  def getString(path: String, default: String)   = ifHasPath(path, default) { _.getString(path) }
  def getInt(path: String, default: Int)         = ifHasPath(path, default) { _.getInt(path) }
  def getConfig(path: String, default: Config)   = ifHasPath(path, default) { _.getConfig(path) }
  def getMilliseconds(path: String, default: Long) = ifHasPath(path, default) {
    _.getDuration(path, TimeUnit.MILLISECONDS)
  }
  def getOptionalString(path: String, default: Option[String] = None) = getOptional(path) { _.getString(path) }

  def loadDefault(rootName: String, loadEnvConf: Boolean = true) =
    if (loadEnvConf) {
      ConfigFactory
        .parseResources(s"env-conf/$getHostname.conf")
        .withFallback(ConfigFactory.load())
        .getConfig(rootName)
    } else {
      ConfigFactory.load().getConfig(rootName)
    }

  protected def getHostname = InetAddress.getLocalHost.getHostName

  private def ifHasPath[T](path: String, default: T)(get: Config => T): T =
    if (rootConfig.hasPath(path)) get(rootConfig) else default

  private def getOptional[T](fullPath: String, default: Option[T] = None)(get: Config => T) =
    if (rootConfig.hasPath(fullPath)) {
      Some(get(rootConfig))
    } else {
      default
    }
}
