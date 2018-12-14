package org.ditw.exutil1.naen
import org.json4s.DefaultFormats

case class NaEn(neid:Long, name:String, aliases:Vector[String], gnid:Long) {

}

object NaEn extends Serializable {
  def toJsons(pocos:Array[NaEn]):String = {
    import org.json4s.jackson.Serialization._
    writePretty(pocos)(DefaultFormats)
  }


}