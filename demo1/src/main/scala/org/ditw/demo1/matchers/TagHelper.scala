package org.ditw.demo1.matchers

object TagHelper extends Serializable {

  private val AdmTmPfx = "TmAdm_"
  def admTmTag(adm0Code:String):String = AdmTmPfx + adm0Code
  private val Adm1SubTmPfx = "TmAdm1Sub_"
  def adm1SubEntTmTag(adm0Code:String):String = Adm1SubTmPfx + adm0Code
  private val _DynamicTmPfx = "_DyAdm_"
  def admDynTag(adm0Code:String):String = _DynamicTmPfx + adm0Code
  private val CmAdm1SubPfx = "_CmAdm1Sub_"
  def adm1AndSubCmTag(adm0Code:String):String = CmAdm1SubPfx + adm0Code

}
