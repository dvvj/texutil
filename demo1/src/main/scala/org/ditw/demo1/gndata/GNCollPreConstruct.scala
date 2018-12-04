package org.ditw.demo1.gndata
import java.text.Normalizer

import scala.collection.mutable.ListBuffer

object GNCollPreConstruct extends Serializable {

  private[gndata] type GNCollPreProc = Map[String, TGNColl] => Unit

  private val jpAdm1Suffixes = Set(
    "-ken", "prefecture"
  )

  private val jpAdmSfx = Set(
    "shi", "gun", "ku", "machi", "cho", "mura"
  )
  private val jpAdm2SuffixesSorted = jpAdmSfx.flatMap { sfx =>
      List(sfx, s"-$sfx")
  }.toList.sortBy(_.length)(Ordering[Int].reverse)

  private def jpAdm2Alias(
                           lowerName:String,
                           sfxFound:String,
                           lst:ListBuffer[String]
                         ):Unit = {
    val trimmed = lowerName.substring(0, lowerName.length - sfxFound.length).trim
    lst += s"$trimmed-city"
    lst += trimmed
    if (jpAdmSfx.contains(sfxFound)) {
      lst += s"$trimmed-$sfxFound"
    }
//    if (sfxFound == "shi") // todo
//      lst += s"$trimmed-shi"
//    if (sfxFound == "gun")
//      lst += s"$trimmed-gun"
//    if (sfxFound == "ku")
//      lst += s"$trimmed-ku"
  }

  private val replRegex = "\\p{M}".r
  private def normalize(n:String):Option[String] = {
    val nr = Normalizer.normalize(n, Normalizer.Form.NFD)
    if (nr == n) None
    else {
      Option(replRegex.replaceAllIn(nr, ""))
    }
  }


  import GNCntry._
  private[gndata] val _ppMap = Map[GNCntry, GNCollPreProc] (
    JP -> (m => {
      m.foreach { p =>
        val (_, coll) = p
        if (coll.self.nonEmpty) {
          val admEnt = coll.self.get
          val lowerOrig = admEnt.name.toLowerCase()
          val normedName = normalize(lowerOrig)
          val lower = normedName.getOrElse(lowerOrig)
          if (p._2.level == GNLevel.ADM1) {

            var found = false
            var trimmed = lower
            val it = jpAdm1Suffixes.iterator
            while (!found && it.hasNext) {
              val sfx = it.next()
              if (lower.endsWith(sfx)) {
                found = true
                trimmed = lower.substring(0, lower.length - sfx.length).trim
              }
            }
            if (found) {
              admEnt.addAliases(List(trimmed))
            }
          } else { // if (p._2.level == GNLevel.ADM2) {
            var found = false
            var sfxFound = ""
            val it = jpAdm2SuffixesSorted.iterator
            while (!found && it.hasNext) {
              val sfx = it.next()
              if (lower.endsWith(sfx)) {
                found = true
                sfxFound = sfx
              }
            }
            if (found) {
              val alias2Add = ListBuffer[String]()
              jpAdm2Alias(lowerOrig, sfxFound, alias2Add)
              if (normedName.nonEmpty) {
                jpAdm2Alias(normedName.get, sfxFound, alias2Add)
              }
              admEnt.addAliases(alias2Add)
            }
          }

        }
      }
    }),
    US -> (m => {
      m.foreach { p =>
        val (_, coll) = p
        if (coll.self.nonEmpty) {
          val admEnt = coll.self.get
          if (p._2.level == GNLevel.ADM1) {
            val alias = admEnt.admCodes(0)
            admEnt.addAliases(List(alias))
          }
        }
      }
    })
  )

  private[gndata] def preprocess(cntry: GNCntry, admMap:Map[String, TGNColl]):Unit = {
    if (_ppMap.contains(cntry)) {
      _ppMap(cntry)(admMap)
    }
  }

}
