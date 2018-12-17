package org.ditw.sparkRuns.csvXtr
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row}
import org.apache.spark.storage.StorageLevel
import org.ditw.exutil1.naen.NaEn
import org.ditw.sparkRuns.csvXtr.UtilsEntCsv3.{ColISNI, ColName, rowInfo}

object EntXtrUtils extends Serializable {
  def taggedErrorMsg(tag:Int, msg:String):Option[String] = {
    Option(s"{$tag} $msg")
  }

  type RawRowProcessRes[R] = (String, Option[R], Option[String])
  def process[T](df: DataFrame,
                 rowFunc:Row => RawRowProcessRes[T],
                 entFunc:(RawRowProcessRes[T], Long) => NaEn
                ):(Array[NaEn], Array[String]) = {
    val t = df.rdd.map(rowFunc)
      .persist(StorageLevel.MEMORY_AND_DISK_SER_2)

    val ents = t.filter(_._2.nonEmpty)
      .sortBy(_._1)
      .zipWithIndex()
      .map(p => entFunc(p._1, p._2))
      .collect()
    println(s"Ent   #: ${ents.length}")
    val errors = t.filter(_._2.isEmpty)
      .sortBy(_._1)
      .map { p =>
        val (ri, _, errMsg) = p
        s"$ri: ${errMsg.get}"
      }.collect()
    println(s"Error #: ${errors.length}")
    ents -> errors
  }

//  def errorRes[T](rowInfo:String, errMsg:String):(String, Option[T], Option[String]) = {
//    (
//      rowInfo, None, Option(errMsg)
//    )
//  }
}
