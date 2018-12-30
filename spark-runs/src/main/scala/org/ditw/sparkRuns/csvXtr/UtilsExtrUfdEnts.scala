package org.ditw.sparkRuns.csvXtr
import java.io.FileOutputStream
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.apache.spark.sql.Row
import org.apache.spark.storage.StorageLevel
import org.ditw.common.{ResourceHelpers, SparkUtils}
import org.ditw.demo1.gndata.GNCntry.{PR, US}
import org.ditw.demo1.gndata.{GNCntry, GNEnt}
import org.ditw.ent0.{UfdEnt, UfdGeo}
import org.ditw.exutil1.naen.NaEnData.NaEnCat
import org.ditw.exutil1.naen.{NaEn, NaEnData, SrcCsvMeta}
import org.ditw.sparkRuns.CommonUtils
import org.ditw.sparkRuns.pmXtr.PmXtrUtils

object UtilsExtrUfdEnts {
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

  import EntXtrUtils._
  import org.ditw.ent0.UfdType._

  def main(args:Array[String]):Unit = {
    val spSess = SparkUtils.sparkSessionLocal()
    val ccs = Set(US)

    import CommonUtils._
    import GNCntry._
    val gnmmgr = loadGNMmgr(
      ccs,
      Set(),
      spSess.sparkContext,
      "file:///media/sf_vmshare/gns/all")

    val brGNMmgr = spSess.sparkContext.broadcast(gnmmgr)

    val rows = csvRead(
      spSess,
      "/media/sf_vmshare/ringgold_isni.csv",
      csvMeta
    ).persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val nameGnHints = ResourceHelpers.load("/isni_gnhint.json", IsniGnHint.load)
      .map { h => h.strInName.toLowerCase() -> h.gnid }.toMap
    val brNameHint = spSess.sparkContext.broadcast(nameGnHints)

    val idStart = NaEnData.catIdStart(NaEnCat.UFD)

    val EmptyEnts = List[GNEnt]()
    type RowResType = (String, GNCntry, List[String], GNEnt, Map[String, String])
    val (ents, errors) = process[RowResType](
      rows,
      row => {
        val cc = csvMeta.strVal(row, ColCountryCode)
        var res:Option[RowResType] = None
        var errMsg:Option[String] = None
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
            var ents =
              if (h.nonEmpty) {
                if (h.size > 1) {
                  throw new RuntimeException("todo: multiple hints?")
                }
                else {
                  val gnidHint = h.head._2
                  val hintEnt = brGNMmgr.value.svc.entById(gnidHint)
                  if (hintEnt.nonEmpty) {
                    val hint = allEnts.filter(PmXtrUtils._checkGNidByDist(hintEnt.get, _))
                    println(s"Found hint for [$name]: $hint")
                    hint
                  }
                  else EmptyEnts
                }
              }
              else allEnts

            if (ents.size > 1) {
              val maxPop = ents.map(_.population).max
              ents = ents.filter(_.population == maxPop)
            }

            if (ents.size == 1) {
              val ent = ents.head
              val altName = csvMeta.altNames(row)
              val altNames =
                if (altName == null || altName.isEmpty)
                  List[String]()
                else List(altName)
              res = Option((name, GNCntry.withName(cc), altNames, ent,
                Map(
                  NaEn.Attr_CC -> cc
                ) ++ csvMeta.otherKVPairs(row)
              ))
            }
            else {
              if (ents.nonEmpty) {
                val maxPop = ents.map(_.population).max
                errMsg = taggedErrorMsg(4, s"todo: more than one ents (max population: $maxPop): $ents")
              }
              else {
                errMsg = taggedErrorMsg(3, s"$cityStateCC not found (filtered)")
              }
            }
          }
        }
        val ri = rowInfo(row)
        (ri, res, errMsg)

      },
      (tp, idx) => {
        val orgLId = idStart + idx
        val (ri, res, _) = tp
        val (name, cntry, alts, ent, attrs) = res.get
        val geo = UfdEnt.ent2UfdGeo(ent) // UfdGeo(cntry, Array(), ent.gnid)
        val id = UfdEnt.ufdId(TODO, geo, orgLId)
        NaEn(id, name, alts, ent.gnid, attrs)
      }
    )

    println(s"Ents: ${ents.length}")

//    val errorOut = new FileOutputStream("/media/sf_vmshare/isni_err.txt")
//    IOUtils.write(errors.mkString("\n"), errorOut, StandardCharsets.UTF_8)
//    errorOut.close()

    spSess.stop()
  }



}
