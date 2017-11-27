package schemer.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import com.fasterxml.jackson.annotation.JsonInclude.Include

import scala.reflect.ClassTag

private[schemer] object JSONUtil {
  private val mapper = new ObjectMapper()

  mapper.registerModule(DefaultScalaModule)

  mapper.setSerializationInclusion(Include.NON_NULL)

  def toJson(value: Any) = mapper.writeValueAsString(value)

  def fromJson[T: ClassTag](json: String) = {
    val classType = implicitly[ClassTag[T]].runtimeClass.asInstanceOf[Class[T]]
    mapper.readValue[T](json, classType)
  }
}
