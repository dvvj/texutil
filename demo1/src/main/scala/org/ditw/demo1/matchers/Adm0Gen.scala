package org.ditw.demo1.matchers
import org.ditw.common.Dict
import org.ditw.demo1.gndata.TGNMap
import org.ditw.matcher.{TTkMatcher, TokenMatchers}

object Adm0Gen extends Serializable {

  private val EmptyPairs = Iterable[(String, String)]()
  def genTms(adm0:TGNMap, dict:Dict):Iterable[TTkMatcher] = {
    val name2Admc = adm0.admMap.flatMap { p =>
      val admCode = p._1
      if (p._2.self.nonEmpty) {
        p._2.self.get.queryNames.map(_ -> admCode)
      }
      else EmptyPairs
    }

    val tmAdms = TokenMatchers.ngramExtraTag(name2Admc, dict, TagHelper.admTmTag(adm0.countryCode.toString))
    Iterable(tmAdms)
  }

  //def genCms()
}
