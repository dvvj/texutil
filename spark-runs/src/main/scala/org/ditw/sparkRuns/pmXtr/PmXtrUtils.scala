package org.ditw.sparkRuns.pmXtr
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.rdd.RDD
import org.apache.spark.storage.StorageLevel
import org.ditw.common.TkRange
import org.ditw.demo1.gndata.GNCntry.GNCntry
import org.ditw.demo1.gndata.GNEnt
import org.ditw.exutil1.naen.NaEnData.UsUnivColls
import org.ditw.exutil1.naen.TagHelper.NaEnId_Pfx
import org.ditw.exutil1.naen.{NaEn, NaEnData, TagHelper}
import org.ditw.matcher.{MatchPool, TkMatch}
import org.ditw.pmxml.model.AAAuAff
import org.ditw.sparkRuns.CommonUtils.GNMmgr
import org.ditw.textSeg.common.Tags.TagGroup4Univ
import org.json4s.DefaultFormats

object PmXtrUtils extends Serializable {
  private[pmXtr] def segment(affStr:String):Array[String] = {
    affStr.split(";").map(_.trim).filter(!_.isEmpty)
  }

  type SegmentRes = (Long, Int, Array[String])
  private[pmXtr] def segmentInput(spark: SparkContext, inputPath:String):RDD[SegmentRes] = {
    spark.textFile(inputPath)
      .flatMap { l =>
        val firstComma = l.indexOf(",")
        val pmid = l.substring(1, firstComma).toLong
        val j = l.substring(firstComma + 1, l.length - 1)
        val auaff = AAAuAff.fromJson(j)
        val affMap = auaff.affs.map(aff => (pmid, aff.localId, segment(aff.aff.aff)))
        affMap
      }
  }

  case class SingleSegRes(
    pmid:Long,
    localId:Int,
    seg:String,
    rangeStr:String,
    naen:NaEn
  ) {
    override def toString: String = {
      s"$pmid-$localId: [$seg] [$naen]"
    }
  }

  def singleRes2Json(ssr:SingleSegRes):String = {
    import org.json4s.jackson.Serialization._
    write(ssr)(DefaultFormats)
  }

  private def xtrNaEns(matchPool: MatchPool):Set[Long] = {
    val tags = matchPool.allTagsPrefixedBy(NaEnId_Pfx)
    val matches = matchPool.get(tags.toSet)
    val merged = TkMatch.mergeByRange(matches)
    merged.flatMap(_.getTags).filter(_.startsWith(NaEnId_Pfx))
      .map(_.substring(NaEnId_Pfx.length).toLong)
  }

  type RawRes = (String, Boolean, Option[SingleSegRes])
  private[pmXtr] def processSingleSegs(
    singleSegs:RDD[SegmentRes],
    brGNMmgr:Broadcast[GNMmgr],
    brCcs:Broadcast[Set[GNCntry]]
  ):(RDD[SingleSegRes], RDD[String]) = {
    val ccsStr = brCcs.value.map(_.toString)
    val brCcsStr = singleSegs.sparkContext.broadcast(ccsStr)
    val t = singleSegs.map { tp3 =>
      val (pmid, localId, affSegs) = tp3
      if (pmid == 24555113L && localId == 0)
        println("ok")
      val aff = affSegs(0) // single line
      val gnm = brGNMmgr.value
      val mp = MatchPool.fromStr(aff, gnm.tknr, gnm.dict)
      gnm.mmgr.run(mp)
      val univRngs = mp.get(TagGroup4Univ.segTag).map(_.range)
      val rng2Ents = gnm.svc.extrEnts(gnm.xtrMgr, mp)
      val ents = rng2Ents.values.flatten
      val entsOfCntry = ents.filter(ent => brCcsStr.value.contains(ent.countryCode))
      var res:Option[SingleSegRes] = None

      if (entsOfCntry.nonEmpty) {
        val gnids = entsOfCntry.map(_.gnid).toSet
        val univs =
          if (univRngs.size == 1 && rng2Ents.size == 1) { // name fix, todo: better structure
            val univRng = univRngs.head
            val gnEntRng = rng2Ents.head._1
            if (univRng.overlap(gnEntRng) && gnEntRng.start > univRng.start) {
              val newRng = univRng.copy(end = gnEntRng.start)
              Set(newRng.str)
            }
            else {
              univRngs.map(_.str)
            }
          }
          else univRngs.map(_.str)

        import TagHelper._
        if (univs.size > 1)
          println(s"more than 1 seg found: $univs")
        val neids = xtrNaEns(mp)
        val ents = neids.flatMap(brGNMmgr.value.naEntDataMap.get)
        val entsByGNid = ents.filter(e => gnids.contains(e.gnid))
        // todo: could be multiple entities with diff ids while pointing to the same entity
        val entsTr = entsByGNid.map { e =>
          val neid = e.neid
          val gnEnt = brGNMmgr.value.svc.entById(e.gnid).get
          s"$neid(${gnEnt.gnid}:${gnEnt.name})"
        }

        if (entsByGNid.nonEmpty) {
          val univStr = if (univs.nonEmpty) univs.head else ""
          val singleRes = SingleSegRes(pmid, localId, univStr, rng2Ents.keySet.head.str, entsByGNid.head)
          //println(s"Univs: [${univs.mkString(",")}] NEIds: [${entsTr.mkString(",")}]")
          res = Option(singleRes)
        }
        //else None
      }
      //else None
      (s"$pmid-$localId: $aff", entsOfCntry.nonEmpty, res)

    }.persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val found = t.flatMap(_._3)
    val empty = t.filter(tp => tp._2 && tp._3.isEmpty).map(_._1)
    found -> empty
  }
}
