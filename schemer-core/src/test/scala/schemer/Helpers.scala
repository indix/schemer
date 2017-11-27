package schemer

import java.net.URI

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.{FileSystem, Path}

case class TestRecord(title: String, url: String, storeId: Int)

object Helpers {

  def cleanOutputPath(output: String) {
    val outputPath = new Path(output)
    if (fileExists(output))
      outputPath.getFileSystem(new Configuration()).delete(outputPath, true)
  }

  def fileExists(fileLocation: String) = {
    val fs = FileSystem.get(new URI(fileLocation), new Configuration())
    fs.exists(new Path(fileLocation))
  }
}
