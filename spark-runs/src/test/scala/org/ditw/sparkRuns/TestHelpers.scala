package org.ditw.sparkRuns
import org.apache.spark.SparkContext
import org.ditw.common.ResourceHelpers
import org.ditw.demo1.gndata.GNCntry.GNCntry
import org.ditw.demo1.gndata.GNSvc
import org.ditw.sparkRuns.CommonUtils.GNMmgr

object TestHelpers extends Serializable {

  import org.ditw.demo1.gndata.GNCntry._
  private val _gnLines = ResourceHelpers.loadStrs("/gns/test_gns.csv")
  private[sparkRuns] val _ccs = Set(US, PR)
  private val _ccms = Set(PR)

  import CommonUtils._
  def testGNMmgr(
                 spark:SparkContext
                ):GNMmgr = {
    val gnLines = spark.parallelize(_gnLines)
    _loadGNMmgr(_ccs, _ccms, spark, gnLines, Map())
  }
}
