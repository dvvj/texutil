package org.ditw.nameUtils.nameParser

import MedlineParsers.RegisteredParsers
import ParserHelpers.InputKeyEnum._
import ParserHelpers._

/**
  * Created by dev on 2017-09-08.
  */
object PaymentParsers extends Serializable {

  private val EmptyStr = ""
  private def trimStr(s:String):String = if (s != null) s.trim else EmptyStr

  private[nameParser] def getNormInputPayment(
    firstName:String,
    middleName:String,
    lastName:String,
    suffix:String
  ):Option[NormInput] = {
    val ln = trimStr(lastName)

    if (ln.isEmpty) None
    else {
      val fns = List(firstName, middleName).filter(p => p != null).map(_.trim).filter(!_.isEmpty)
      val fn = combineNameComponents(fns)
      val sf = trimStr(suffix)
      //if (suffix != null && !suffix.isEmpty) Option(suffix.trim)
      Option(genNormInput(
        lastName, fn,
        if (sf.isEmpty) None else Option(sf)
      ))
    }
  }

}
