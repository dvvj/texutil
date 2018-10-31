package org.ditw.common

class Dict(
  private val map:Map[String, Int]
) extends Serializable {
  import Dict._
  private val revMap:Map[Int, String] = map.map(p => p._2 -> p._1)
  val size:Int = map.size
  def contains(w:String):Boolean = map.contains(w.toLowerCase())
  def enc(w:String):Int = map.getOrElse(w.toLowerCase(), UNKNOWN)
  def dec(c:Int):String = revMap(c)
}

object Dict extends Serializable {
  val UNKNOWN = Int.MinValue
}