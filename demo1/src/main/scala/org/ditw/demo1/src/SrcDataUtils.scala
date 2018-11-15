package org.ditw.demo1.src

object SrcDataUtils extends Serializable {

  object GNsCols extends Enumeration {
    type GNsCols = Value
    val GID,
      Name,
      AsciiName,
      AltNames,
      Latitude,
      Longitude,
      FeatureClass,
      FeatureCode,
      CountryCode,
      CountryCode2,
      Adm1,
      Adm2,
      Adm3,
      Adm4,
      Population,
      Elevation,
      Dem,
      Timezone,
      UpdateDate = Value
  }

  import GNsCols._

  private val GNsColArr = Array(
    GID, Name, AsciiName, AltNames, Latitude, Longitude,
    FeatureClass, FeatureCode, CountryCode, CountryCode2,
    Adm1, Adm2, Adm3, Adm4, Population,
    Elevation, Dem, Timezone, UpdateDate
  )

  private val GNsColIdx2Enum = GNsColArr.indices
    .map(idx => idx -> GNsColArr(idx))
    .toMap
  private val GNsColEnum2Idx = GNsColArr.indices
    .map(idx => GNsColArr(idx) -> idx)
    .toMap

  def GNsCol(line:Array[String], col:GNsCols):String =
    line(GNsColEnum2Idx(col))

  private val AllIncluded = Set[String]()
  private val AdmClass = "A"
  private val PplClass = "P"
  private val featureClass2CodesMap = Map(
    AdmClass -> AllIncluded, // A: country, state, region,...
    PplClass -> AllIncluded, // P: city, village,...
    "L" -> Set( // L: parks,area, ...
      "CTRB" // business center
    )
  )

  type FeatureChecker = (String, String) => Boolean

  private val admExcludedCodes = Set(
    "ADM1H",
    "ADM2H",
    "ADM3H",
    "ADM4H",
    "ADM5H",
    "ADMDH",
    "PCLH"
  )
  val fcAdm:FeatureChecker = (fcls:String, fcode:String) => {
    fcls == AdmClass && !admExcludedCodes.contains(fcode)
  }
  val fcPpl:FeatureChecker = (fcls:String, fcode:String) => {
    fcls == PplClass
  }
  val fcAll:FeatureChecker = (fcls:String, fcode:String) => {
    fcAdm(fcls, fcode) || fcPpl(fcls, fcode) ||
      (featureClass2CodesMap.contains(fcls) && featureClass2CodesMap(fcls).contains(fcode))
  }
}
