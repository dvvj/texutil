package org.ditw.sparkRuns.csvXtr
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.spark.sql.Row
import org.apache.spark.storage.StorageLevel
import org.ditw.common.{ResourceHelpers, SparkUtils}
import org.ditw.demo1.gndata.GNCntry
import org.ditw.demo1.gndata.GNCntry.{PR, US}
import org.ditw.exutil1.naen.NaEnData.NaEnCat
import org.ditw.exutil1.naen.{NaEn, NaEnData, SrcCsvMeta}
import org.ditw.sparkRuns.CommonUtils

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

//  private def errorRes(row:Row, errMsg:String):
//    (String, Option[(String, Vector[String], Long, Map[String, String])], Option[String]) = {
//    (
//      rowInfo(row), None, Option(errMsg)
//    )
//  }

  import EntXtrUtils._
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

    val nameGnHints = ResourceHelpers.load("/isni_gnhint.json", IsniGnHint.load)
      .map { h => h.strInName.toLowerCase() -> h.gnid }.toMap
    val brNameHint = spSess.sparkContext.broadcast(nameGnHints)

    type RowResType = (String, List[String], Long, Map[String, String])
    val (ents, errors) = process[RowResType](
      rows,
      row => {
        val cc = csvMeta.strVal(row, ColCountryCode)
        var errMsg:Option[String] = None
        var res:Option[RowResType] = None
        if (!GNCntry.contains(cc)) {
          errMsg = taggedErrorMsg(1, s"Country code [$cc] not found")
        }
        else if (!ccs.contains(GNCntry.withName(cc))) {
          errMsg = taggedErrorMsg(2, s"Country code [$cc] not included")
        }
        else {
          val cityStateCC = csvMeta.gnStr(row)
          val rng2Ents = extrGNEnts(cityStateCC, brGNMmgr.value, false)

          if (rng2Ents.isEmpty) {
            errMsg = taggedErrorMsg(3, s"$cityStateCC not found")
          }
          else {
            //val nearest = checkNearestGNEnt(rng2Ents.values.flatten, row, "LATITUDE", "LONGITUDE")
            val allEnts = rng2Ents.values.flatten

            val name = csvMeta.name(row)
            val lowerName = name.toLowerCase()
            val h = brNameHint.value.filter(p => name.toLowerCase().contains(p._1))
            val ents =
              if (h.nonEmpty) {
                if (h.size > 1) {
                  throw new RuntimeException("todo: multiple hints?")
                }
                else {
                  val gnidHint = h.head._2
                  val hint = allEnts.filter(_.gnid == gnidHint)
                  println(s"Found hint for [$name]: $hint")
                  hint
                }
              }
              else allEnts
            if (ents.size == 1) {
              val ent = ents.head
              val altName = csvMeta.altNames(row)
              val altNames =
                if (altName == null || altName == "NOT AVAILABLE" || altName.isEmpty)
                  List[String]()
                else List(altName)
              res = Option((name, altNames, ent.gnid,
                Map(
                  NaEn.Attr_CC -> cc
                ) ++ csvMeta.otherKVPairs(row)
              ))
            }
            else {
               errMsg = taggedErrorMsg(4, s"todo: more than one ents: $ents")
            }
          }
        }
        val ri = rowInfo(row)
        (ri, res, errMsg)
      },
      (tp, idx) => {
        val id = idStart + idx
        val (ri, res, _) = tp
        val (name, alts, gnid, attrs) = res.get
        NaEn(id, name, alts, gnid, attrs)
      }
    )
//    val preRes = rows.rdd.map { row =>
//      val cc = csvMeta.strVal(row, ColCountryCode)
//      var errMsg:Option[String] = None
//      var res:Option[(String, Vector[String], Long, Map[String, String])] = None
//      if (!GNCntry.contains(cc)) {
//        errMsg = taggedErrorMsg(1, s"Country code [$cc] not found")
//      }
//      else if (!ccs.contains(GNCntry.withName(cc))) {
//        errMsg = taggedErrorMsg(2, s"Country code [$cc] not included")
//      }
//      else {
//        val cityStateCC = csvMeta.gnStr(row)
//        val rng2Ents = extrGNEnts(cityStateCC, brGNMmgr.value, false)
//
//        if (rng2Ents.isEmpty) {
//          errMsg = taggedErrorMsg(3, s"$cityStateCC not found")
//        }
//        else {
//          //val nearest = checkNearestGNEnt(rng2Ents.values.flatten, row, "LATITUDE", "LONGITUDE")
//          val allEnts = rng2Ents.values.flatten
//
//          if (allEnts.size == 1) {
//            val ent = allEnts.head
//            val name = csvMeta.name(row)
//            val altName = csvMeta.altNames(row)
//            val altNames =
//              if (altName == null || altName == "NOT AVAILABLE" || altName.isEmpty)
//                Vector[String]()
//              else Vector(altName)
//            res = Option((name, altNames, ent.gnid, Map(NaEn.Attr_CC -> cc)))
//          }
//          else {
//            errMsg = taggedErrorMsg(4, s"todo: more than one ents: $allEnts")
//          }
//        }
//      }
//      val ri = rowInfo(row)
//      (ri, res, errMsg)
//    }.persist(StorageLevel.MEMORY_AND_DISK_SER_2)
//
//    val allResults = preRes.filter(_._2.nonEmpty)
//      .sortBy(_._1)
//      .zipWithIndex()
//      .map { p =>
//        val (tp, idx) = p
//        val id = idStart + idx
//        val (ri, res, _) = tp
//        val (name, alts, gnid, attrs) = res.get
//        NaEn(id, name, alts.toArray, gnid, attrs)
//      }
//
//    println(s"#: ${allResults.count()}")
//
//    val allEnts = allResults.collect()
    writeJson(
      "/media/sf_vmshare/isni.json",
      ents, NaEn.toJsons
    )

//    val allErrors = preRes.filter(_._2.isEmpty)
//      .sortBy(_._1)
//      .map { p =>
//        val (ri, _, errMsg) = p
//        s"$ri: ${errMsg.get}"
//      }
//    val allErrs = allErrors.collect().mkString("\n")
    val errorOut = new FileOutputStream("/media/sf_vmshare/isni_err.txt")
    IOUtils.write(errors.mkString("\n"), errorOut, StandardCharsets.UTF_8)
    errorOut.close()

    spSess.stop()
  }
}
