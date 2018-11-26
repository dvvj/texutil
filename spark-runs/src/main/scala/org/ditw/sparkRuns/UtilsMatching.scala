package org.ditw.sparkRuns
import org.apache.spark.storage.StorageLevel
import org.ditw.common.SparkUtils
import org.ditw.demo1.gndata.GNCntry
import org.ditw.demo1.gndata.GNCntry._
import org.ditw.demo1.gndata.SrcData.{loadAdm0, loadCountries, tabSplitter}
import org.ditw.demo1.matchers.{Adm0Gen, MatcherGen, TagHelper}
import org.ditw.matcher.MatchPool
import org.ditw.tknr.TknrHelpers

object UtilsMatching extends App {
  val spark = SparkUtils.sparkContextLocal()

  val gnLines = spark.textFile(
    //"/media/sf_vmshare/fp2Affs_uniq"
    "/media/sf_vmshare/gns/all"
  ).map(tabSplitter.split)
    .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

  val adm0Ents = loadAdm0(gnLines)
  val brAdm0Ents = spark.broadcast(adm0Ents)

  val ccs = Set(
    US
    ,CA, GB, AU //,FR,DE,ES,IT
  )
  val adm0s = loadCountries(gnLines, ccs, brAdm0Ents)

  val dict = MatcherGen.loadDict(adm0s.values)

  val matchers = adm0s.values.map { adm0 =>
    Adm0Gen.genMatchers(adm0, dict)
  }

  val mmgr = MatcherGen.gen(adm0s.values, dict)

  val brMmgr = spark.broadcast(mmgr)
  val brDict = spark.broadcast(dict)
  val brTknr = spark.broadcast(TknrHelpers.TknrTextSeg)

  spark.textFile("/media/sf_vmshare/aff-w2v")
    .foreach { l =>
      val mp = MatchPool.fromStr(l, TknrHelpers.TknrTextSeg, brDict.value)
      brMmgr.value.run(mp)
      val t = TagHelper.cityCountryCmTag(GNCntry.US)
      val res = mp.get(t)
      println(res.size)
    }

  spark.stop()
}
