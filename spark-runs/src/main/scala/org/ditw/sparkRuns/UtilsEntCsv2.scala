package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.demo1.gndata.GNCntry
import org.ditw.demo1.gndata.GNCntry.{PR, US}
import org.ditw.exutil1.naen.{NaEn, NaEnData}
import org.ditw.exutil1.naen.NaEnData.NaEnCat

import org.ditw.sparkRuns.UtilsEntCsv1.{Pfx2Replace, processName}

object UtilsEntCsv2 {
  def main(args:Array[String]):Unit = {
    val spSess = SparkUtils.sparkSessionLocal()

    val headers =
      "X,Y,OBJECTID,IPEDSID,NAME,ADDRESS,CITY,STATE,ZIP,ZIP4," +
        "TELEPHONE,TYPE,STATUS,POPULATION,COUNTY,COUNTYFIPS,COUNTRY," +
        "LATITUDE,LONGITUDE,NAICS_CODE,NAICS_DESC,SOURCE,SOURCEDATE," +
        "VAL_METHOD,VAL_DATE,WEBSITE,STFIPS,COFIPS,SECTOR,LEVEL_," +
        "HI_OFFER,DEG_GRANT,LOCALE,CLOSE_DATE,MERGE_ID,ALIAS," +
        "SIZE_SET,INST_SIZE,PT_ENROLL,FT_ENROLL,TOT_ENROLL,HOUSING," +
        "DORM_CAP,TOT_EMP,SHELTER_ID"

    import CommonUtils._
    val gnmmgr = loadGNMmgr(Set(US, PR), Set(PR), spSess.sparkContext, "file:///media/sf_vmshare/gns/all")

    val brGNMmgr = spSess.sparkContext.broadcast(gnmmgr)

    val rows = csvRead(
      spSess, "/media/sf_vmshare/Colleges_and_Universities.csv",
      "NAME", "CITY", "STATE", "LATITUDE", "LONGITUDE", "ALIAS"
    )
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
    val idStart = NaEnData.catIdStart(NaEnCat.US_UNIV)

    val res = rows.rdd.flatMap { row =>
      val gnm = brGNMmgr.value
      val city = row.getAs[String]("CITY")
      val state = row.getAs[String]("STATE")
      val cityState = s"$city $state"
      var rng2Ents = runStr(
        cityState, gnm.tknr, gnm.dict, gnm.mmgr, gnm.svc, gnm.xtrMgr
      )

      if (rng2Ents.isEmpty) {
        val repl = replPfx(cityState, Pfx2Replace)
        if (repl.nonEmpty) {
          rng2Ents = runStr(
            repl.get,
            gnm.tknr, gnm.dict, gnm.mmgr, gnm.svc,
            gnm.xtrMgr
          )
        }
      }

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
            val altName = row.getAs[String]("ALIAS")
            val altNames =
              if (altName == null || altName.isEmpty || altName == "NOT AVAILABLE")
                Array[String]()
              else Array(altName)
            Option((name, altNames, nearest.get.gnid))
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
      "/media/sf_vmshare/us_univ_coll.json",
      hosps, NaEn.toJsons
    )

    spSess.close()
  }

  private val Pfx2Replace = Map(
    "ST " -> "SAINT ",
    "ST. " -> "SAINT ",
    "MT. " -> "MOUNT ",
    "MC " -> "MC"
  )
}
