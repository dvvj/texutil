package org.ditw.nameUtils.nameParser.langSpecData

/**
  * Created by dev on 2017-09-05.
  */
object ChineseLangData extends Serializable {

  private val lastNameBlackList = Set(
    "de Man",
    "de Juan",
    "de Die"
  )

  private val nameBlackList = Set(
    "duncan",
    "lee",
    "sean"
  )

  def isLastNameBlacklisted(lastName:String):Boolean = lastNameBlackList.contains(lastName)
  def isBlacklisted(foreNameOrLastName:String):Boolean =
    nameBlackList.contains(foreNameOrLastName.toLowerCase)

  private[nameParser] val MaxChineseNameComps = 3
}
