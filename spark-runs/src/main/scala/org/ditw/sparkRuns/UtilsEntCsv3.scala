package org.ditw.sparkRuns
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.spark.sql.Row
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

  private def rowInfo(row:Row):String = {
    val isni = row.getAs[String](ColISNI)
    val name = row.getAs[String](ColName)
    s"$name($isni)"
  }

  private def errorRes(row:Row, errMsg:String):
    (String, Option[(String, Vector[String], Long, Map[String, String])], Option[String]) = {
    (
      rowInfo(row), None, Option(errMsg)
    )
  }

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
    val preRes = rows.rdd.map { row =>
      val cc = csvMeta.strVal(row, ColCountryCode)
      var errMsg = ""
      var res:Option[(String, Vector[String], Long, Map[String, String])] = None
      if (!GNCntry.contains(cc)) {
        errMsg = s"{1} Country code [$cc] not found"
      }
      else if (!ccs.contains(GNCntry.withName(cc))) {
        errMsg = s"{2} Country code [$cc] not included"
      }
      else {
        val cityStateCC = csvMeta.gnStr(row)
        val rng2Ents = extrGNEnts(cityStateCC, brGNMmgr.value, false)

        if (rng2Ents.isEmpty) {
          errMsg = s"{3} $cityStateCC not found"
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
                Vector[String]()
              else Vector(altName)
            res = Option((name, altNames, ent.gnid, Map(NaEn.Attr_CC -> cc)))
          }
          else {
            errMsg = s"{4} todo: more than one ents: $allEnts"
          }
        }
      }
      val ri = rowInfo(row)
      val r = res
      val em = if (r.isEmpty) Option(errMsg) else None
      (ri, r, em)
    }.persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val allResults = preRes.filter(_._2.nonEmpty)
      .sortBy(_._1)
      .zipWithIndex()
      .map { p =>
        val (tp, idx) = p
        val id = idStart + idx
        val (ri, res, _) = tp
        val (name, alts, gnid, attrs) = res.get
        NaEn(id, name, alts.toArray, gnid, attrs)
      }

    println(s"#: ${allResults.count()}")

    val allEnts = allResults.collect()
    writeJson(
      "/media/sf_vmshare/isni.json",
      allEnts, NaEn.toJsons
    )

    val allErrors = preRes.filter(_._2.isEmpty)
      .sortBy(_._1)
      .map { p =>
        val (ri, _, errMsg) = p
        s"$ri: ${errMsg.get}"
      }
    val allErrs = allErrors.collect().mkString("\n")
    val errorOut = new FileOutputStream("/media/sf_vmshare/isni_err.txt")
    IOUtils.write(allErrs, errorOut, StandardCharsets.UTF_8)
    errorOut.close()

    spSess.stop()
  }
}
