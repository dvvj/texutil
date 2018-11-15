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

}
