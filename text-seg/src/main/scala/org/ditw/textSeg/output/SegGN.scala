package org.ditw.textSeg.output
import org.json4s.DefaultFormats

case class AffGN(
  gnid:Long,
  gnrep:String,
  pmAffFps:Array[String]
)
case class SegGN(
  name:String,
  affGns:Array[AffGN]
) {

}

object SegGN extends Serializable {
  def toJson(segGn:SegGN):String = {
    import org.json4s.jackson.Serialization._
    write(segGn)(DefaultFormats)
  }

  def fromJson(j:String):SegGN = {
    import org.json4s.jackson.JsonMethods._
    implicit val fmt = DefaultFormats
    parse(j).extract[SegGN]
  }
}