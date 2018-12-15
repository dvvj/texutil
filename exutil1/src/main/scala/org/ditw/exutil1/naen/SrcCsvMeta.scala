package org.ditw.exutil1.naen
import org.apache.spark.sql.Row

case class SrcCsvMeta(
  nameCol:String,
  altNamesCol:String,
  coordCols:Option[(String, String)],
  gnCols:Vector[String],
  others:Vector[String] = SrcCsvMeta.EmptyCols
  ) {

  private val _allCols:Vector[String] = {
    val t = Vector(nameCol, altNamesCol) ++ gnCols ++ others
    if (coordCols.nonEmpty) t ++ Vector(coordCols.get._1, coordCols.get._2)
    else t
  }
  def allCols:Vector[String] = _allCols

  def gnStr(row:Row):String = {
    gnCols.map(row.getAs[String]).filter(_ != null).mkString(" ")
  }

  def name(row:Row):String = strVal(row, nameCol)
  def altNames(row:Row):String = strVal(row, altNamesCol)

  def strVal(row:Row, col:String):String = row.getAs[String](col)

  def latCol:String = coordCols.get._1
  def lonCol:String = coordCols.get._2
}

object SrcCsvMeta extends Serializable {
  val EmptyCols:Vector[String] = Vector()
}
