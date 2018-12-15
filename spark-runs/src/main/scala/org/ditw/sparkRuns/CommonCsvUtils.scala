package org.ditw.sparkRuns
import org.apache.spark.sql.Row

object CommonCsvUtils extends Serializable {

  def concatCols(row:Row, col:String*):String = {
    col.map(row.getAs[String]).mkString(" ")
  }
}
