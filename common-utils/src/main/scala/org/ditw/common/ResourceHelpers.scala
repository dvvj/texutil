package org.ditw.common
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils

object ResourceHelpers extends Serializable {
  def loadStrs(path:String):Array[String] = {
    val instrm = getClass.getResourceAsStream(path)
    val content = IOUtils.toString(instrm, StandardCharsets.UTF_8)
    instrm.close()
    content.split("\\n+")
      .map(_.trim)
      .filter(!_.isEmpty)
  }
}
