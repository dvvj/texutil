package org.ditw.demo1.gndata
import org.apache.spark.rdd.RDD
import org.ditw.common.TkRange
import org.ditw.demo1.gndata.GNCntry.{CA, GNCntry, US}
import org.ditw.demo1.gndata.SrcData.{loadAdm0, loadCountries}
import org.ditw.demo1.src.SrcDataUtils
import org.ditw.extract.XtrMgr
import org.ditw.matcher.MatchPool

import scala.collection.mutable.ListBuffer

class GNSvc private (private[demo1] val _cntryMap:Map[GNCntry, TGNMap]) extends Serializable {
  private val idMap:Map[Long, GNEnt] = {
    _cntryMap.flatMap(_._2.idMap)
  }

  def entById(gnid:Long):Option[GNEnt] = {
    idMap.get(gnid)
  }

  import GNSvc._
  def extrEnts(xtrMgr:XtrMgr[Long], matchPool: MatchPool):Map[TkRange, List[GNEnt]] = {
    val xtrs = xtrMgr.run(matchPool)
    xtrs.mapValues(_.flatMap(entById))
      .mapValues(mergeEnts)
  }
}

object GNSvc extends Serializable {

  private def checkIfContains(ent1:GNEnt, ent2:GNEnt):Option[GNEnt] = {
    if (ent1.countryCode == ent2.countryCode &&
      ent1.admCodes.length <= ent2.admCodes.length
    ) {
      val admCodesChecked = ent1.admCodes.indices.forall(
        idx => ent1.admCodes(idx) == ent2.admCodes(idx)
      )
      if (!admCodesChecked) None
      else if (ent1.admCodes.length < ent2.admCodes.length) Option(ent2)
      else {
        val ent1IsAdm = SrcDataUtils.isAdm(ent1)
        val ent2IsAdm = SrcDataUtils.isAdm(ent2)
        if (ent1IsAdm && !ent2IsAdm) Option(ent2)
        else if (ent1IsAdm && ent2IsAdm) {
          println(s"both adms?! $ent1 -- $ent2")
          None
        }
          // throw new IllegalArgumentException("both adms?!")
        else Option(ent1)
      }
    }
    else None
  }

  private def mergeEnts(ents:List[GNEnt]):List[GNEnt] = {
    if (ents.size <= 1)
      ents
    else {
      var head = ents.head
      var tail = ents.tail
      val res = ListBuffer[GNEnt]()
      while (tail.nonEmpty) {
        val itTail = tail.iterator
        var headMerged = false
        val remTail = ListBuffer[GNEnt]()
        while (!headMerged && itTail.hasNext) {
          val t = itTail.next()
          val c = checkIfContains(head, t)
          if (c.nonEmpty) {
            if (c.get.eq(t)) {
              headMerged = true
              remTail += t
            }
            else {
              // tail merged, do not add to remTail
            }
          }
          else {
            remTail += t
          }
        }
        remTail ++= itTail

        if (!headMerged) res += head
        if (remTail.nonEmpty) {
          head = remTail.head
          tail = remTail.tail.toList
        }
        else tail = Nil
      }
      res.toList
    }

  }

  def load(
    lines:RDD[Array[String]],
    countries:Set[GNCntry]
  ): GNSvc = {
    val spark = lines.sparkContext
    val adm0Ents = loadAdm0(lines)
    val brAdm0Ents = spark.broadcast(adm0Ents)

    val ccs = Set(
      US, CA
      //, "GB", "AU", "FR", "DE", "ES", "IT"
    )
    val adm0s = loadCountries(lines, ccs, brAdm0Ents)

    new GNSvc(adm0s)
  }
}