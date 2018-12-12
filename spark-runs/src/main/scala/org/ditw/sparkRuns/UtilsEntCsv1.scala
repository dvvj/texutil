package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.demo1.gndata.GNCntry

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
    val gnmmgr = loadGNMmgr(Set(GNCntry.US), spSess.sparkContext, "file:///media/sf_vmshare/gns/all")

    val brGNMmgr = spSess.sparkContext.broadcast(gnmmgr)

    val rows = csvRead(
        spSess, "/media/sf_vmshare/Hospitals.csv",
        "NAME", "CITY", "STATE", "LATITUDE", "LONGITUDE", "ALT_NAME"
      )
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val res = rows.rdd.flatMap { row =>
      val gnm = brGNMmgr.value
      val city = row.getAs[String]("CITY")
      val state = row.getAs[String]("STATE")
      val cityState = s"$city $state"
      val rng2Ents = runStr(
        cityState, gnm.tknr, gnm.dict, gnm.mmgr, gnm.svc, gnm.xtrMgr
      )
      if (rng2Ents.isEmpty) {
        println(s"$cityState not found")
        None
      }
      else {
        Option(rng2Ents.values)
      }
//      else {
//        println(s"Found: ${rng2Ents.values.mkString(",")}")
//      }

    }

    println(s"#: ${res.count()}")

    spSess.stop()
  }
}
