package org.ditw.sparkRuns

object NaEnIds extends Serializable {

  object NaEnCat extends Enumeration {
    type NaEnCat = Value
    val US_UNIV, US_HOSP = Value
  }

  import NaEnCat._

  private val idStartCatMap:Map[NaEnCat, Long] = Map(
    US_UNIV -> 1000000000L,
    US_HOSP -> 2000000000L
  )

  def catIdStart(cat:NaEnCat):Long = idStartCatMap(cat)

}
