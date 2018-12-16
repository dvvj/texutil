package org.ditw.exutil1.naen
import org.json4s.DefaultFormats

case class NaEn(
  neid:Long,
  name:String,
  aliases:Array[String],
  gnid:Long,
  exAttrs:Map[String, String] = NaEn.EmptyAttrs
) {

}

object NaEn extends Serializable {
  def toJsons(pocos:Array[NaEn]):String = {
    import org.json4s.jackson.Serialization._
    writePretty(pocos)(DefaultFormats)
  }

  def fromJsons(j:String):Array[NaEn] = {
    import org.json4s.jackson.JsonMethods._
    implicit val fmt = DefaultFormats
    parse(j).extract[Array[NaEn]]
  }

  val Attr_CC:String = "CC"

  val EmptyAttrs:Map[String, String] = Map()
}