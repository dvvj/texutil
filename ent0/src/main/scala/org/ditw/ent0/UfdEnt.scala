package org.ditw.ent0
import org.ditw.demo1.gndata.GNCntry.GNCntry
import org.ditw.demo1.gndata.{GNCntry, GNEnt}
import org.ditw.ent0.UfdType.UfdType

case class UfdEnt(
                   id:Long,
                   isni:Option[String]
                 ) {

}

object UfdEnt extends Serializable {
  private def pieceId(
    typePart:Int,
    cntryPart:Int,
    admsPart:Int,
    orgPart:Int,
    subOrgPart:Int
  ):Long = {
    var x = (typePart.toLong << 3) << 9
    x = x | cntryPart

    x = x << 12
    x = x | admsPart

    x = x << 20
    x = x | orgPart

    x = x << 16
    x = x | subOrgPart

    x
  }

  import UfdType._
  private val Type2IdPartMap = Map(
    TODO -> 7,
    Rsvd -> 0,
    Educational -> 1,
    Operational -> 2,
    Industrial -> 3,
    Researching -> 4
  )
  import org.ditw.demo1.gndata.GNCntry._
  private val Cntry2IdPartMap = Map(
    US -> 1,
    PR -> 2,
    CA -> 4,
    GB -> 5,
    AU -> 6
  )
  def ufdId(
    t:UfdType,
    geo:UfdGeo,
    orgLId:Long
  ):Long = {
    pieceId(
      Type2IdPartMap(t),
      Cntry2IdPartMap(geo.cntry),
      0xFFF, // todo
      orgLId.toInt,
      0xFFFF
    )
  }

  private type AdmCodeXtrer = GNEnt => Array[String]
  private val admCodeXtrerMap = Map[GNCntry, AdmCodeXtrer](
    US -> (ent => ent.admCodes.slice(0, 1).toArray)
  )

  def ent2UfdGeo(ent: GNEnt):UfdGeo = {
    val cntry = GNCntry.withName(ent.countryCode)
    val admCodes = admCodeXtrerMap(cntry)(ent)
    UfdGeo(cntry, admCodes, ent.gnid)
  }
}