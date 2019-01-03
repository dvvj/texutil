package org.ditw.sparkRuns.csvXtr
import org.json4s.DefaultFormats

case class IsniErrata(isni:String, col:String, v:String) {

}

object IsniErrata extends Serializable {
  def load(json:String):Array[IsniErrata] = {
    import org.json4s.jackson.JsonMethods._
    implicit val fmt = DefaultFormats
    val p = parse(json)
    p.extract[Array[IsniErrata]]
  }
}