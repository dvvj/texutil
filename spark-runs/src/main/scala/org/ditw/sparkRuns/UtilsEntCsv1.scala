package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.{GenUtils, ResourceHelpers, SparkUtils}
import org.ditw.demo1.gndata.GNCntry
import org.ditw.exutil1.naen.{NaEn, NaEnData}
import org.ditw.exutil1.naen.NaEnData.NaEnCat

import scala.collection.mutable.ListBuffer

object UtilsEntCsv1 {
  def main(args:Array[String]):Unit = {
    val spSess = SparkUtils.sparkSessionLocal()

    val headers =
      "X,Y,OBJECTID,ID,NAME,ADDRESS," +
      "CITY,STATE,ZIP,ZIP4,TELEPHONE," +
      "TYPE,STATUS,POPULATION,COUNTY," +
      "COUNTYFIPS,COUNTRY,LATITUDE,LONGITUDE," +
      "NAICS_CODE,NAICS_DESC,SOURCE,SOURCEDATE," +
      "VAL_METHOD,VAL_DATE,WEBSITE,STATE_ID," +
      "ALT_NAME,ST_FIPS,OWNER,TTL_STAFF,BEDS,TRAUMA,HELIPAD"


    import CommonUtils._
    import GNCntry._
    val gnmmgr = loadGNMmgr(Set(US, PR), Set(PR), spSess.sparkContext, "file:///media/sf_vmshare/gns/all")

    val brGNMmgr = spSess.sparkContext.broadcast(gnmmgr)

    val rows = csvRead(
        spSess, "/media/sf_vmshare/Hospitals.csv",
        "NAME", "CITY", "STATE", "LATITUDE", "LONGITUDE", "ALT_NAME"
      )
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val idStart = NaEnData.catIdStart(NaEnCat.US_HOSP)

    import CommonCsvUtils._
    val res = rows.rdd.flatMap { row =>
      val cityState = concatCols(row, "CITY", "STATE")
      val rng2Ents = extrGNEnts(cityState, brGNMmgr.value, Pfx2Replace)

        if (rng2Ents.isEmpty) {
          println(s"$cityState not found")
          None
        }
        else {
          if (rng2Ents.values.size != 1) {
            throw new RuntimeException(s"------ more than one ents: $rng2Ents")
          }
          else {
            val lat = row.getAs[String]("LATITUDE").toDouble
            val lon = row.getAs[String]("LONGITUDE").toDouble
            val nearest = findNearestAndCheck(rng2Ents.values.head, lat->lon)

            if (nearest.nonEmpty) {
              val name = row.getAs[String]("NAME")
              val altName = row.getAs[String]("ALT_NAME")
              val altNames = if (altName == null || altName == "NOT AVAILABLE" || altName.isEmpty)
                Array[String]()
              else Array(altName)
              Option((processName(name), altNames, nearest.get.gnid))
            }
            else {
              None  //todo trace
            }

          }
        }
      }
      .sortBy(_._1)
      .zipWithIndex()
      .map { p =>
        val (tp, idx) = p
        val id = idStart + idx
        NaEn(id, tp._1, tp._2, tp._3)
      }
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    println(s"#: ${res.count()}")

    val hosps = res.collect()
    writeJson(
      "/media/sf_vmshare/us_hosp.json",
      hosps, NaEn.toJsons
    )

    spSess.stop()
  }

  private val dashTokenSet = ResourceHelpers.loadStrs("/hosp_name_with_dash.txt")
    .map(_.toLowerCase()).toSet
  private val spaceSplitter = "\\s+".r
  private val dash = '-'
  private def isDash(s:String):Boolean = s == "-"
  private def processName(name:String):String = {
    val spaceSplits = spaceSplitter.split(name.toLowerCase())
    val tokens = spaceSplits.flatMap { s =>
      if (dashTokenSet.contains(s)) List(s)
      else {
        if (!isDash(s)) {
          GenUtils.splitPreserve(s, dash)
        }
        else
          List(s)
      }
    }
    val firstDash = tokens.indices.find(idx => isDash(tokens(idx)))
    if (firstDash.nonEmpty) {
      val s = tokens.slice(0, firstDash.get)
      if (s.length > 2)
        s.mkString(" ")
      else // probably a wrong slice
        name.toLowerCase()
    }
    else name.toLowerCase()
  }

  private val Pfx2Replace = Map(
    "ST " -> "SAINT ",
    "ST. " -> "SAINT ",
    "MT. " -> "MOUNT ",
    "MC " -> "MC"
  )
}
