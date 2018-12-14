package org.ditw.exutil1.naen
import org.ditw.common.{Dict, ResourceHelpers}
import org.ditw.common.InputHelpers.splitVocabEntries
import org.ditw.matcher.{TTkMatcher, TokenMatchers}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object NaEnData extends Serializable {

//  trait NaEnInfo extends Serializable {
//
//  }
  import collection.mutable

  object NaEnCat extends Enumeration {
    type NaEnCat = Value
    val US_UNIV, US_HOSP = Value
  }

  import NaEnCat._

  private val idStartCatMap:Map[NaEnCat, Long] = Map(
    US_UNIV -> 1000000000L,
    US_HOSP -> 2000000000L
  )

  def catIdStart(cat:NaEnCat):Long = idStartCatMap(cat)


  private def splitNames(ne:NaEn):Set[Array[String]] = {
    val allNames = mutable.Set[String]()
    allNames += ne.name
    allNames ++= ne.aliases
    allNames.filter(n => n != null && n.nonEmpty)
    splitVocabEntries(allNames.toSet)
  }
  def vocab4NaEns(d:Array[NaEn]):Iterable[Iterable[String]] = {

    val res = ListBuffer[Array[String]]()
    d.foreach { e =>
      res ++= splitNames(e)
    }
    res.map(_.toVector).toList
  }

  private def tm4NaEns(d:Array[NaEn], dict:Dict, tag:String): TTkMatcher = {
    val name2Ids = d.flatMap { ne =>
        val ids = Set(
          TagHelper.NaEnId(ne.neid)
        )
        val allNames = splitNames(ne).map(_.mkString(" ").toLowerCase())
        allNames.map(_ -> ids)
      }
      .groupBy(_._1)
      .mapValues(_.flatMap(_._2).toSet)
    TokenMatchers.ngramExtraTags(
      name2Ids, dict, tag
    )
  }

  private val UsUnivColls = ResourceHelpers.load("/naen/us_univ_coll.json", NaEn.fromJsons)
  private val UsHosps = ResourceHelpers.load("/naen/us_hosp.json", NaEn.fromJsons)
  val allVocs = vocab4NaEns(UsUnivColls) ++ vocab4NaEns(UsHosps)
  import TagHelper._
  private[exutil1] def tmUsUniv(dict:Dict) =
    tm4NaEns(UsUnivColls, dict, builtinTag(US_UNIV.toString))
  private[exutil1] def tmUsHosp(dict:Dict) =
    tm4NaEns(UsHosps, dict, builtinTag(US_HOSP.toString))

  def tmsNaEn(dict:Dict) = List(
    tmUsUniv(dict), tmUsHosp(dict)
  )

}
