package org.ditw.demo1.gndata

object GNCollPreConstruct extends Serializable {

  private[gndata] type GNCollPreProc = Map[String, TGNColl] => Unit

  private val jpAdm1Suffixes = Set(
    "-ken", "prefecture"
  )
  private val jpAdm2SuffixesSorted = List(
    "shi", "-shi", "gun", "-gun"
  ).sortBy(_.length)(Ordering[Int].reverse)


  import GNCntry._
  private[gndata] val _ppMap = Map[GNCntry, GNCollPreProc] (
    JP -> (m => {
      m.foreach { p =>
        val (_, coll) = p
        if (p._2.level == GNLevel.ADM1) {
          if (coll.self.nonEmpty) {
            val adm1Ent = coll.self.get
            val lower = adm1Ent.name.toLowerCase()
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
              adm1Ent.addAliases(List(trimmed))
            }
          }
        }
        else if (p._2.level == GNLevel.ADM2) {
          if (coll.self.nonEmpty) {
            val adm2Ent = coll.self.get
            val lower = adm2Ent.name.toLowerCase()
            var found = false
            var trimmed = lower
            val it = jpAdm2SuffixesSorted.iterator
            while (!found && it.hasNext) {
              val sfx = it.next()
              if (lower.endsWith(sfx)) {
                found = true
                trimmed = lower.substring(0, lower.length - sfx.length).trim
              }
            }
            if (found) {
              val cityAlias = s"$trimmed-city"
              adm2Ent.addAliases(List(cityAlias))
            }
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
