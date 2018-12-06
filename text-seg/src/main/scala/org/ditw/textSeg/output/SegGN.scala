package org.ditw.textSeg.output
import org.json4s.DefaultFormats

case class AffGN(
  gnid:Long,
  gnrep:String,
  pmAffFps:IndexedSeq[String]
)
case class SegGN(
  name:String,
  affGns:IndexedSeq[AffGN]
) {

}

object SegGN extends Serializable {
  def toJson(segGn:SegGN):String = {
    import org.json4s.jackson.Serialization._
    write(segGn)(DefaultFormats)
  }
}