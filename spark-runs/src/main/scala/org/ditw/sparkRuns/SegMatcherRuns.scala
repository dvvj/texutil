package org.ditw.sparkRuns
import java.io.File

import org.apache.commons.io.FileUtils
import org.ditw.textSeg.catSegMatchers.Cat2SegMatchers
import org.ditw.textSeg.common.AllCatMatchers._
import org.ditw.textSeg.common.Tags

object SegMatcherRuns extends App {

  val spark = SparkUtils.sparkContextLocal()

  val affLines = spark.textFile("/media/sf_vmshare/aff-w2v")

  val mmgr = mmgrFrom(
    Cat2SegMatchers.segMatchers
  )
  val brMgr = spark.broadcast(mmgr)
  println(s"Line count: ${affLines.count}")

  val allUnivs = affLines.map { l =>
    val mp = run(brMgr.value, l)
    val matches = mp.get(Tags.TagGroup4Univ.segTag)
    val univNames = matches.map { m =>
      var t = m.range.origStr
      if (t.endsWith(","))
        t = t.substring(0, t.length-1)
      t
    }
    univNames.map(_ -> l)
//    val sorted = matches.toList.sortBy(_.range)
//    println(sorted.size)
//    sorted
  }.filter(_.nonEmpty)
    .cache()

  println(s"Matched Line count: ${allUnivs.count}")

  val savePath = "/media/sf_vmshare/seg2/univs"
  val savePathFile = new File(savePath)
  if (savePathFile.exists()) {
    FileUtils.deleteDirectory(savePathFile)
  }

  allUnivs
    .flatMap(s => s)
    .groupByKey()
    .mapValues(_.mkString("\t", "\n\t", ""))
    .coalesce(1)
    .sortBy(x => x._1)
    .map { p =>
      s"${p._1}\n${p._2}"
    }
    .saveAsTextFile(savePath)

  spark.stop()

}
