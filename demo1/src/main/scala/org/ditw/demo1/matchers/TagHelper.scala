package org.ditw.demo1.matchers
import org.ditw.demo1.gndata.GNCntry.GNCntry

object TagHelper extends Serializable {

  val TmAdm0 = "TmAdm0"
  private val AdmTmPfx = "TmAdm_"
  def admTmTag(cntry: GNCntry):String = AdmTmPfx + cntry
  private val _Adm1SubTmPfx = "TmAdm1Sub_"
  private val _DynamicTmPfx = "_DyAdm_"
  private[demo1] val CityAdm1Pfxs = IndexedSeq(_Adm1SubTmPfx, _DynamicTmPfx)
  def adm1SubEntTmTag(adm1Code:String):String = _Adm1SubTmPfx + adm1Code
  def admDynTag(admCode:String):String = _DynamicTmPfx + admCode
  private val _CountryTmPfx = "_CNTR_"
  def countryTag(cntry: GNCntry):String = _CountryTmPfx + cntry
  private val _CityTmPfx = "_CITY_"
  def countryOfCountryTag(cntry: GNCntry):String = _CityTmPfx + cntry
  private val CmAdm1SubPfx = "_CmAdm1Sub_"
  def adm1AndSubCmTag(adm1c: String):String = CmAdm1SubPfx + adm1c
  private val CmCityCountryPfx = "_CITY_CNTR_"
  def cityCountryCmTag(cntry: GNCntry):String = CmCityCountryPfx + cntry
  private[demo1] val GNIdTagPfx = "GNId_"
  private[demo1] val GNIdTagTmpl = s"$GNIdTagPfx%d"

}
