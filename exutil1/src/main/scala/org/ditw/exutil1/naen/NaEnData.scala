package org.ditw.exutil1.naen
import org.ditw.common.InputHelpers.splitVocabEntries
import org.ditw.matcher.{TTkMatcher, TokenMatchers}

import scala.collection.mutable.ListBuffer

object NaEnData extends Serializable {

//  trait NaEnInfo extends Serializable {
//
//  }
  import collection.mutable
  def vocab4NaEns(d:Array[NaEn]):Iterable[Iterable[String]] = {

  val res = ListBuffer[Array[String]]()
    d.foreach { e =>
      if (e.aliases.nonEmpty)
        res ++= splitVocabEntries(e.aliases.toSet + e.name)
      else
        res ++= splitVocabEntries(Set(e.name))
    }
    res.map(_.toVector).toList
  }

//  def tm4NaEns(d:Array[NaEn], tag:String): TTkMatcher = {
//    TokenMatchers.ngramT()
//  }
}
