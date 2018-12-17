package org.ditw.sparkRuns.csvXtr
import org.apache.spark.storage.StorageLevel
import org.ditw.common.{GenUtils, ResourceHelpers, SparkUtils}
import org.ditw.demo1.gndata.GNCntry
import org.ditw.exutil1.naen.NaEnData.NaEnCat
import org.ditw.exutil1.naen.{NaEn, NaEnData, SrcCsvMeta}
import org.ditw.sparkRuns.{CommonCsvUtils, CommonUtils}

object UtilsEntCsv1 extends Serializable {

  private val headers =
    "X,Y,OBJECTID,ID,NAME,ADDRESS," +
      "CITY,STATE,ZIP,ZIP4,TELEPHONE," +
      "TYPE,STATUS,POPULATION,COUNTY," +
      "COUNTYFIPS,COUNTRY,LATITUDE,LONGITUDE," +
      "NAICS_CODE,NAICS_DESC,SOURCE,SOURCEDATE," +
      "VAL_METHOD,VAL_DATE,WEBSITE,STATE_ID," +
      "ALT_NAME,ST_FIPS,OWNER,TTL_STAFF,BEDS,TRAUMA,HELIPAD"
  private val csvMeta = SrcCsvMeta(
    "NAME",
    "ALT_NAME",
    Option("LATITUDE", "LONGITUDE"),
    Vector("CITY", "STATE")
  )

  def main(args:Array[String]):Unit = {
    val spSess = SparkUtils.sparkSessionLocal()
    import GNCntry._
    val ccs = Set(US, PR)

    import CommonUtils._

    val gnmmgr = loadGNMmgr(ccs, Set(PR), spSess.sparkContext, "file:///media/sf_vmshare/gns/all")

    val brGNMmgr = spSess.sparkContext.broadcast(gnmmgr)

    val rows = csvRead(
        spSess,
      "/media/sf_vmshare/Hospitals.csv",
        csvMeta
      )
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val idStart = NaEnData.catIdStart(NaEnCat.US_HOSP)

    import CommonCsvUtils._
    val res = rows.rdd.flatMap { row =>

        val cityState = csvMeta.gnStr(row)
        val rng2Ents = extrGNEnts(cityState, brGNMmgr.value, true, Pfx2Replace)

        if (rng2Ents.isEmpty) {
          println(s"$cityState not found")
          None
        }
        else {
          val nearest = checkNearestGNEnt(rng2Ents.values.flatten, row, csvMeta.latCol, csvMeta.lonCol)
          if (nearest.nonEmpty) {
            val name = csvMeta.name(row)
            val altName = csvMeta.altNames(row)
            val altNames = if (altName == null || altName == "NOT AVAILABLE" || altName.isEmpty) //todo
              Array[String]()
            else Array(altName)
            Option((processName(name), altNames, nearest.get.gnid))
          }
          else {
            None  //todo trace
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
