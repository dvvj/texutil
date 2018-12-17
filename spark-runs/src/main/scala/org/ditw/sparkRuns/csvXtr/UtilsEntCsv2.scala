package org.ditw.sparkRuns.csvXtr
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.spark.sql.Row
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.demo1.gndata.GNCntry.{PR, US}
import org.ditw.exutil1.naen.NaEnData.NaEnCat
import org.ditw.exutil1.naen.{NaEn, NaEnData, SrcCsvMeta}
import org.ditw.sparkRuns.{CommonCsvUtils, CommonUtils}

object UtilsEntCsv2 extends Serializable {

  val headers =
    "X,Y,OBJECTID,IPEDSID,NAME,ADDRESS,CITY,STATE,ZIP,ZIP4," +
      "TELEPHONE,TYPE,STATUS,POPULATION,COUNTY,COUNTYFIPS,COUNTRY," +
      "LATITUDE,LONGITUDE,NAICS_CODE,NAICS_DESC,SOURCE,SOURCEDATE," +
      "VAL_METHOD,VAL_DATE,WEBSITE,STFIPS,COFIPS,SECTOR,LEVEL_," +
      "HI_OFFER,DEG_GRANT,LOCALE,CLOSE_DATE,MERGE_ID,ALIAS," +
      "SIZE_SET,INST_SIZE,PT_ENROLL,FT_ENROLL,TOT_ENROLL,HOUSING," +
      "DORM_CAP,TOT_EMP,SHELTER_ID"

  private val csvMeta = SrcCsvMeta(
    "NAME",
    "ALIAS",
    Option("LATITUDE", "LONGITUDE"),
    Vector("CITY", "STATE")
  )

  private def rowInfo(row:Row):String = {
    row.getAs[String](csvMeta.nameCol)
  }

  import EntXtrUtils._
  def main(args:Array[String]):Unit = {
    val spSess = SparkUtils.sparkSessionLocal()


    import CommonUtils._
    val gnmmgr = loadGNMmgr(Set(US, PR), Set(PR), spSess.sparkContext, "file:///media/sf_vmshare/gns/all")

    val brGNMmgr = spSess.sparkContext.broadcast(gnmmgr)

    val rows = csvRead(
      spSess,
      "/media/sf_vmshare/Colleges_and_Universities.csv",
      csvMeta
    )
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
    val idStart = NaEnData.catIdStart(NaEnCat.US_UNIV)
    import CommonCsvUtils._

    type RowResType = (String, Vector[String], Long)
    val (ents, errors) = process[RowResType](
      rows,
      row => {
        val cityState = csvMeta.gnStr(row)
        val rng2Ents = extrGNEnts(cityState, brGNMmgr.value, true, Pfx2Replace)

        var errMsg:Option[String] = None
        var res:Option[RowResType] = None
        if (rng2Ents.isEmpty) {
          errMsg = taggedErrorMsg(1, s"$cityState not found")
        }
        else {
          val nearest = checkNearestGNEnt(rng2Ents.values.flatten, row, csvMeta.latCol, csvMeta.lonCol)

          if (nearest.nonEmpty) {
            val name = csvMeta.name(row)
            val altName = csvMeta.altNames(row)
            val altNames =
              if (altName == null || altName.isEmpty || altName == "NOT AVAILABLE")
                Vector[String]()
              else Vector(altName)
            res = Option((name, altNames, nearest.get.gnid))
          }
          else {
            errMsg = taggedErrorMsg(2, s"Nearest not found for $cityState, candidates: $rng2Ents")
          }
        }
        (rowInfo(row), res, errMsg)
      },
      (tp, idx) => {
        val (name, alias, gnid) = tp._2.get
        val id = idStart + idx
        NaEn(id, name, alias.toArray, gnid)
      }
    )
//    val res = rows.rdd.map
//      .sortBy(_._1)
//      .zipWithIndex()
//      .map
//      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
//
//    println(s"#: ${res.count()}")
//
//    val hosps = res.collect()
    writeJson(
      "/media/sf_vmshare/us_univ_coll.json",
      ents, NaEn.toJsons
    )
    val errorOut = new FileOutputStream("/media/sf_vmshare/us_univ_coll_err.txt")
    IOUtils.write(errors.mkString("\n"), errorOut, StandardCharsets.UTF_8)
    errorOut.close()

    spSess.close()
  }

  private val Pfx2Replace = Map(
    "ST " -> "SAINT ",
    "ST. " -> "SAINT ",
    "MT. " -> "MOUNT ",
    "MC " -> "MC"
  )
}
