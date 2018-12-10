package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.demo1.gndata.GNCntry.GB
import org.ditw.demo1.gndata.{GNEnt, GNSvc}
import org.ditw.demo1.gndata.SrcData.tabSplitter
import org.ditw.sparkRuns.UtilPocoCsv1.minMax

import scala.collection.mutable.ListBuffer

object UtilPoco1Map2GNs {
  private val maxDiff = 0.25
  private def checkCoord(
    lat1:Double,
    long1:Double,
    lat2:Double,
    long2:Double
  ):Boolean = {
    math.abs(lat1-lat2) < maxDiff && math.abs(long1-long2) < maxDiff
  }

  private def distByCoord(
    lat1:Double,
    long1:Double,
    lat2:Double,
    long2:Double
  ):Double = {
    val latDiff = lat1-lat2
    val lonDiff = long1-long2
    latDiff*latDiff + lonDiff*lonDiff
  }

  private def removeBrackets(in:String):String = {
    val rightIdx = in.lastIndexOf(')')
    if (rightIdx >= 0) {
      val leftIdx = in.lastIndexOf('(', rightIdx)
      if (leftIdx >= 0)
        in.substring(0, leftIdx).trim
      else in
    }
    else in
  }

  def main(args:Array[String]):Unit = {
    val spSess = SparkUtils.sparkSessionLocal()

    val gnLines = spSess.sparkContext.textFile(
      //"/media/sf_vmshare/fp2Affs_uniq"
      "file:///media/sf_vmshare/gns/all"
    ).map(tabSplitter.split)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)
    val ccs = Set(
      GB
      //,CA , GB, AU //,FR,DE,ES,IT
    )
    val svc = GNSvc.loadNoPopuReq(gnLines, ccs)
    val ents = svc.entsByName(GB, "Hythe")
    println(ents.length)
    val brSvc = spSess.sparkContext.broadcast(svc)

    val headers = "Postcode,Latitude,Longitude,Easting,Northing,Grid Ref,Postcodes,Active postcodes,Population,Households,Built up area"
      .split(",")

    val rows = spSess.read
      .format("csv")
      .option("header", "true")
      .load("/media/sf_vmshare/postcode sectors.csv")

    import collection.mutable
    val grouped = rows.select("Postcode", "Latitude", "Longitude", "Built up area")
      .filter(_.get(3) != null)
      .rdd.map(r => removeBrackets(r.getAs[String](3)) -> (r.getAs[String](0), r.get(1).toString.toDouble, r.get(2).toString.toDouble))
      .groupByKey()
      .map { p =>
        val (name, l) = p
        val allEnts = brSvc.value.entsByName(GB, name)
        if (allEnts.nonEmpty) {
          val matchedMap = mutable.Map[Long, mutable.Set[String]]()
          l.foreach { tp =>
            val (poco, lat, lon) = tp
            val nearestEnt = allEnts.minBy(ent => distByCoord(ent.latitude, ent.longitude, lat, lon))
            if (!matchedMap.contains(nearestEnt.gnid))
              matchedMap += nearestEnt.gnid -> mutable.Set[String]()
            matchedMap(nearestEnt.gnid) += poco
          }
          name -> matchedMap.mapValues(_.toVector.sorted).toMap
        }
        else
          name -> Map[Long, Vector[String]]()
      }
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val nonEmpty = grouped.filter(_._2.nonEmpty)
    println(s"Non Empty: ${nonEmpty.count()}")
    val empty = grouped.filter(_._2.isEmpty)
    println(s"Empty: ${empty.count()}")
    empty.foreach { emp => println(emp._1) }

    nonEmpty.flatMap { p =>
        val (name, m) = p
        val tr = m.map(p => s"${p._1}: ${p._2.mkString(",")}")
          .mkString("\t", "\n\t", "")
        s"$name\n$tr"
      }
      .saveAsTextFile("/media/sf_vmshare/pocomap")

    spSess.stop()
  }
}
