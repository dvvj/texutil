package org.ditw.sparkRuns.pmnames
import org.ditw.common.SparkUtils
import org.ditw.sparkRuns.pmXtr.PmXtrUtils

object NParsing {
  import org.ditw.nameUtils.nameParser.ParserHelpers.parseMedlineNameJ
  def main(args:Array[String]):Unit = {

    val spark = SparkUtils.sparkContextLocal()
    val in = "file:///media/sf_vmshare/pmjs/pmj8AuAff"
    val out = "/media/sf_vmshare/pmjs/psd8"

    val pmid2NameSeq = PmXtrUtils.namesFromInput(spark, in)

    val parsedTr = pmid2NameSeq.map { p =>
      val (pmid, auSeq) = p

      //println(pmid)
      val parsed = auSeq.indices.map { idx =>
        val au = auSeq(idx)
        val np = parseMedlineNameJ(au.foreName, au.lastName, "")
        //println(f"\t$idx%03d: [${np.firstName}|${np.middleName}|${np.lastName}|${np.signature}]")
        val tr = f"\t$idx%03d: [${np.firstName}|${np.middleName}|${np.lastName}|${np.signature}]"
        tr
      }
      s"$pmid\n${parsed.mkString("\n")}"
    }


    SparkUtils.deleteLocal(out)
    parsedTr.saveAsTextFile(out)

    spark.stop()
  }

}
