package schemer.registry.server

import com.typesafe.config.Config
import java.util.concurrent.TimeUnit.SECONDS
import scala.concurrent.duration._

trait InferenceConfig extends ConfigWithDefault {
  def rootConfig: Config
  lazy val inferenceConfig = rootConfig.getConfig("inference")
  lazy val inferTimeout    = inferenceConfig.getDuration("timeout", SECONDS).seconds
}
