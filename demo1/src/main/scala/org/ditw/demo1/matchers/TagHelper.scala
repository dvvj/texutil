package org.ditw.demo1.matchers

object TagHelper extends Serializable {

  private val AdmTmPfx = "TmAdm_"
  def admTmTag(adm0Code:String):String = AdmTmPfx + adm0Code

}
