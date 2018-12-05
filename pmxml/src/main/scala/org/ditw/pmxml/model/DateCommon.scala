package org.ditw.pmxml.model

import com.lucidchart.open.xtract.{ XmlReader, __ }
import Constants.XmlTags._
import cats.syntax.all._

import DateCommon._
case class DateCommon(
                     year:Option[Int],
                     private val _month:Option[String],
                     day:Option[Int],
                     medlineDate:Option[String]
                     ) {
  val month:Option[Int] = {

    if (_month.isEmpty)
      None
    else {
      val monStr = _month.get
      val monVal = if (monStr.forall(_.isDigit)) {
        monStr.toInt
      }
      else {
        monthMap(monStr)
      }
      Option(monVal)
    }
  }

}

object DateCommon {
  private val monthMap:Map[String, Int] = Map(
    List("Jan") -> 1,
    List("Feb") -> 2,
    List("Mar") -> 3,
    List("Apr") -> 4,
    List("May") -> 5,
    List("Jun") -> 6,
    List("Jul") -> 7,
    List("Aug") -> 8,
    List("Sept", "Sep") -> 9,
    List("Oct") -> 10,
    List("Nov") -> 11,
    List("Dec") -> 12
  ).flatMap(kv => kv._1.map(_ -> kv._2))

  implicit val reader:XmlReader[DateCommon] = (
    (__ \ Year).read[Int].optional,
    (__ \ Month).read[String].optional,
    (__ \ Day).read[Int].optional,
    (__ \ MedlineDate).read[String].optional
  ).mapN(apply)
}
