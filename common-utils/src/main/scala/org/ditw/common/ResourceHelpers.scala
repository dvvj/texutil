package org.ditw.common
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils

object ResourceHelpers extends Serializable {
  private val EmptyStrs = Array[String]()
  def loadStrs(path:String, throwIfNotFound:Boolean = true):Array[String] = {
    val instrm = getClass.getResourceAsStream(path)
    if (instrm != null) {
      val content = IOUtils.toString(instrm, StandardCharsets.UTF_8)
      instrm.close()
      content.split("\\n+")
        .map(_.trim)
        .filter(!_.isEmpty)
    }
    else {
      if (!throwIfNotFound)
        EmptyStrs
      else
        throw new IllegalArgumentException(s"Resource [$path] not found!")
    }

  }
}
