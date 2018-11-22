package org.ditw.demo1
import org.apache.spark.storage.StorageLevel
import org.ditw.common.{Dict, ResourceHelpers, SparkUtils}
import org.ditw.demo1.gndata.SrcData._
import org.ditw.demo1.gndata.TGNMap
import org.ditw.demo1.gndata.GNCntry._
import org.ditw.demo1.matchers.MatcherGen

object TestData {

  val testCountries:Map[GNCntry, TGNMap] = {
    val spark = SparkUtils.sparkContextLocal()

    val lines = ResourceHelpers.loadStrs("/test_gns.csv")

    val gnLines = spark.parallelize(lines).map(tabSplitter.split)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val adm0Ents = loadAdm0(gnLines)
    val brAdm0Ents = spark.broadcast(adm0Ents)

    val ccs = Set(
      US, CA
      //, "GB", "AU", "FR", "DE", "ES", "IT"
    )
    val adm0s = loadCountries(gnLines, ccs, brAdm0Ents)

    spark.stop()

    adm0s
  }

  val testDict: Dict = MatcherGen.loadDict(testCountries.values)

}
