package org.ditw.sparkRuns
import org.ditw.sparkRuns.SegMatcherRuns.spark

object UtilsSlimGNs extends Serializable {

  import org.ditw.demo1.src.SrcDataUtils._
  import org.ditw.demo1.src.SrcDataUtils.GNsCols

  private val tabSplitter = "\\t".r
  private val commaSplitter = ",".r
  def main(args:Array[String]):Unit = {
    val spark = SparkUtils.sparkContextLocal()
    val gnLines = spark.textFile(
      //"/media/sf_vmshare/fp2Affs_uniq"
      "/media/sf_vmshare/allCountries/allPpls"
    )

    val slimmed = gnLines.map { l =>
      val line = tabSplitter.split(l)

      val altNameCount = commaSplitter.split(
        GNsCol(line, GNsCols.AltNames)
      ).filter(_.nonEmpty).size

      val newLineCols = IndexedSeq(
        GNsCols.GID,
        GNsCols.Name,
        GNsCols.AsciiName,
        GNsCols.Latitude,
        GNsCols.Longitude,
        GNsCols.FeatureClass,
        GNsCols.FeatureCode,
        GNsCols.CountryCode,
        GNsCols.CountryCode2,
        GNsCols.Adm1,
        GNsCols.Adm2,
        GNsCols.Adm3,
        GNsCols.Adm4,
        GNsCols.Population
      )

      val newLine = newLineCols.map(GNsCol(line, _))

      val res = (newLine :+ altNameCount)
        .mkString("\t")
      res
    }

    println(s"Slimmed ${slimmed.count()}")

    slimmed.saveAsTextFile(
      "/media/sf_vmshare/allCountries/slimmedPpls"
    )

    spark.stop()
  }

}
