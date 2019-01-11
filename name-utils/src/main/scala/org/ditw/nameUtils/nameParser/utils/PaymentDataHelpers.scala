package org.ditw.nameUtils.nameParser.utils

import org.ditw.nameUtils.nameParser.ParserHelpers.{combineNameComponents, splitComponents}

import scala.collection.mutable.ListBuffer

/**
  * Created by dev on 2017-09-20.
  */
object PaymentDataHelpers {
  // for Payment Physician Profiles
  private def checkPair(shorter:String, longer:String):Boolean = {
    shorter.isEmpty || longer.toLowerCase.startsWith(shorter.toLowerCase)
  }

  private def checkNameComponentPair(comp1:String, comp2:String):(String, Option[String]) = {
    if (comp1.length > comp2.length) {
      if (!checkPair(comp2, comp1)) comp1 -> Option(comp2)
      else comp1 -> None
    }
    //    else if (npLower.length == anpLower.length) {
    //      // check if in the format "solorzano-klapprott"
    //      val npComps = splitComponents(npLower)
    //      val anpComps = splitComponents(anpLower)
    //      if (npComps.size != anpComps.size || !npComps.sameElements(anpComps))
    //        buf += s"[$npLower||$anpLower]"
    //      else buf += npLower
    //    }
    else {
      if (!checkPair(comp1, comp2)) comp1 -> Option(comp2) // s"[$comp1||$comp2]"
      else comp2 -> None
    }
  }

  private val EmptyStr = " "
  private val PrioritizedSeparators = List("-", EmptyStr)
  private[nameParser] def collectCompSeparators(part:String, comps:IndexedSeq[String]):IndexedSeq[String] = {
    var idx = 0
    val seps = ListBuffer[String]()
    comps.indices.foreach { cidx =>
      val comp = comps(cidx)
      val s = part.indexOf(comp, idx)
      if (s < 0) throw new IllegalArgumentException(s"Component [$comp] not found in part [$part]")
      val sep = part.substring(idx, s).trim
      seps += (if (sep.isEmpty) EmptyStr else sep)
      idx = s+comp.length
    }
    seps.toIndexedSeq
  }

  private def pickSeparator(seps:String*):String = {
    val hits = seps.filter(PrioritizedSeparators.contains)
    if (hits.nonEmpty) {
      var found = false
      val it = PrioritizedSeparators.iterator
      var r:String = null
      while (!found) {
        val s = it.next()
        if (hits.contains(s)) {
          found = true
          r = s
        }
      }
      r
    }
    else throw new IllegalArgumentException(
      s"Separators not found in dictionary: ${seps.map(s => s"[$s]").mkString(",")}"
    )
  }

  private val EmptySeparators = IndexedSeq[String]()
  private[nameParser] def extractComponentSeparators(
    p1:String,
    p2:String,
    comps1:IndexedSeq[String],
    comps2:IndexedSeq[String]
  ):IndexedSeq[String] = {
    if (comps1.size <= 1 && comps2.size <= 1) EmptySeparators
    else {
      val seps1 = collectCompSeparators(p1, comps1)
      val seps2 = collectCompSeparators(p2, comps2)

      val minSize = Math.min(seps1.size, seps2.size)
      val mergedSeps = (1 until minSize).map { sepIdx =>
        pickSeparator(seps1(sepIdx), seps2(sepIdx))
      }
      val sliceStart = if (minSize > 0) minSize else 1
      val remSeps =
        if (seps1.size > minSize) seps1.slice(sliceStart, seps1.size)
        else seps2.slice(sliceStart, seps2.size)
      mergedSeps ++ remSeps
    }
  }

  def mergeNameParts(namePartPairs:Seq[(String,String)])
    :Seq[(IndexedSeq[(String,Option[String])], IndexedSeq[String])] = {
    namePartPairs.map { p =>
      val (npLower, anpLower) = (p._1, p._2)

      val npComps = splitComponents(npLower)
      val anpComps = splitComponents(anpLower)

      val minSize = Math.min(npComps.size, anpComps.size)
      val c1 = (0 until minSize).map { idx =>
        val comp1 = npComps(idx)
        val comp2 = anpComps(idx)
        checkNameComponentPair(comp1, comp2)
      }
      val c2 =
        if (anpComps.size > minSize) anpComps.slice(minSize, anpComps.size)
        else npComps.slice(minSize, npComps.size)
      // try to keep the component separators such as '-'
      val seps = extractComponentSeparators(npLower, anpLower, npComps, anpComps)

      (c1 ++ c2.map(_ -> None), seps)
    }
    //s"$phId:${cc.mkString(";")}"
  }

  private val _NamePartsNotMerged:Array[String] = null
  def mergeNamePartsJ(nameParts1:Array[String], nameParts2:Array[String]):Array[String] = {
    val r = mergeNameParts(nameParts1.zip(nameParts2))
    if (r.exists(_._1.exists(_._2.nonEmpty))) _NamePartsNotMerged
    else {
      r.map { p =>
        val comps = p._1.map(_._1)
        val seps = p._2
        combineNameComponents(comps, seps)
      }.toArray
    }
  }

  def namePartsNotMerged(mergedPartsResult:Array[String]):Boolean = mergedPartsResult == _NamePartsNotMerged

  // for Payment Physician Profiles
  private def _traceCheckNameParts(phId:String, namePartPairs:Seq[(String,String)]):String = {
    val m = mergeNameParts(namePartPairs)
    val cc = m.map { p =>
      val comps = p._1.map{ cp =>
        if (cp._2.isEmpty) cp._1
        else s"[${cp._1}||${cp._2.get}]"
      }
      val seps = p._2
      combineNameComponents(comps, seps)
//      combineNameComponents(
//        part.map { p =>
//          if (p._2.isEmpty) p._1
//          else s"[${p._1}||${p._2.get}]"
//        }
//      )
    }
    s"$phId:${cc.mkString(";")}"
  }

  def traceCheckNameParts(phId:String, namePartPairs:(String,String)*):String = _traceCheckNameParts(phId, namePartPairs)

  def traceCheckNamePartsJ(phId:Int, nameParts1:Array[String], nameParts2:Array[String]):String = {
    val pairs = nameParts1.zip(nameParts2)
    _traceCheckNameParts(phId.toString, pairs)
  }


}
