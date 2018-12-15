package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.demo1.gndata.GNCntry
import org.ditw.demo1.gndata.GNCntry.{PR, US}
import org.ditw.exutil1.naen.{NaEn, NaEnData, SrcCsvMeta}
import org.ditw.exutil1.naen.NaEnData.NaEnCat
import org.ditw.sparkRuns.UtilsEntCsv1.{Pfx2Replace, processName}

object UtilsEntCsv3 {
  private val headers =
    "isni,name,alt_names,locality,admin_area_level_1_short,post_code,country_code,urls"
  private val ColISNI = "isni"
  private val ColName = "name"
  private val ColAltNames = "alt_names"
  private val ColCity = "locality"
  private val ColAdm1 = "admin_area_level_1_short"
  private val ColPostal = "post_code"
  private val ColCountryCode = "country_code"

  private val csvMeta = SrcCsvMeta(
    ColName,
    ColAltNames,
    None,
    Vector(ColCity, ColAdm1, ColCountryCode),
    Vector(ColISNI, ColPostal)
  )


  def main(args:Array[String]):Unit = {
    val spSess = SparkUtils.sparkSessionLocal()
    val ccs = Set(US, PR)

    import CommonUtils._
    import GNCntry._
    val gnmmgr = loadGNMmgr(
      ccs,
      Set(PR),
      spSess.sparkContext,
      "file:///media/sf_vmshare/gns/all")

    val brGNMmgr = spSess.sparkContext.broadcast(gnmmgr)

    val rows = csvRead(
      spSess,
      "/media/sf_vmshare/ringgold_isni.csv",
      csvMeta
    ).persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val idStart = NaEnData.catIdStart(NaEnCat.ISNI)

    import CommonCsvUtils._
    val res = rows.rdd.flatMap { row =>
      val cc = csvMeta.strVal(row, ColCountryCode)
      if (!GNCntry.contains(cc)) {
        println(s"Country code $cc not found")
        None
      }
      else if (!ccs.contains(GNCntry.withName(cc))) {
        None
      }
      else {
        val cityStateCC = csvMeta.gnStr(row)
        val rng2Ents = extrGNEnts(cityStateCC, brGNMmgr.value, false)

        if (rng2Ents.isEmpty) {
          println(s"$cityStateCC not found")
          None
        }
        else {
          //val nearest = checkNearestGNEnt(rng2Ents.values.flatten, row, "LATITUDE", "LONGITUDE")
          val allEnts = rng2Ents.values.flatten

          if (allEnts.size == 1) {
            val ent = allEnts.head
            val name = csvMeta.name(row)
            val altName = csvMeta.altNames(row)
            val altNames =
              if (altName == null || altName == "NOT AVAILABLE" || altName.isEmpty)
                Array[String]()
              else Array(altName)
            Option((name, altNames, ent.gnid))
          }
          else {
            println(s"------ todo: more than one ents: $allEnts")
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
      "/media/sf_vmshare/isni.json",
      hosps, NaEn.toJsons
    )

    spSess.stop()
  }
}
