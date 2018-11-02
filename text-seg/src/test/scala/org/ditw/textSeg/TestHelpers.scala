package org.ditw.textSeg
import org.ditw.textSeg.Settings._Dict
import org.ditw.tknr.TknrHelpers.{loTFrom, resultFrom}
import org.ditw.tknr.TknrResult

object TestHelpers {

  private [textSeg] def testDataTuple(
    testStr:String,
    testContent:IndexedSeq[IndexedSeq[String]]
  ):(String, TknrResult) = {
    testStr ->
      resultFrom(
        testStr,
        _Dict,
        IndexedSeq(loTFrom(testContent))
      )
  }
}
