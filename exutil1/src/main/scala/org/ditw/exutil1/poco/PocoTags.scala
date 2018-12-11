package org.ditw.exutil1.poco

object PocoTags extends Serializable {
  val PocoCountryPfx:String = "_PocoPfx_"
  def pocoTag(cc:String) = s"$PocoCountryPfx$cc"
}
