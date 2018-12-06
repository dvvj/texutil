package org.ditw.sparkRuns
import org.ditw.common.SparkUtils
import org.ditw.pmxml.model.AAAuAff

object UtilsExtractLongNames {
  import collection.mutable
  def main(args:Array[String]):Unit = {
    val spark = SparkUtils.sparkContextLocal()

    val splitter = "\\s+".r
    val pmidSet = mutable.Set[Long]()
    val sortedLongNames = spark.textFile("/media/sf_vmshare/pmjs/pmj9AuAff")
      .flatMap { l =>
        val firstComma = l.indexOf(",")
        val pmid = l.substring(1, firstComma).toLong
        val j = l.substring(firstComma+1, l.length-1)
        val auaff = AAAuAff.fromJson(j)
        auaff.singleAuthors.filter { au =>
          val foreNameParts = splitter.split(au.foreName)
          val lastNameParts = splitter.split(au.lastName)
          foreNameParts.length + lastNameParts.length > 4
        }.map { au =>
          s"${au.foreName}|${au.lastName}" -> pmid
        }
      }
      .aggregateByKey(pmidSet)(
        (s, id) => s += id,
        (s1, s2) => s1 ++= s2
      )
      .mapValues(_.toVector.sorted)
      .sortBy(_._1)

    val path = "/media/sf_vmshare/pmjs/longnames"
    SparkUtils.del(spark, path)
    sortedLongNames.saveAsTextFile(path)

    spark.stop()
  }
}
