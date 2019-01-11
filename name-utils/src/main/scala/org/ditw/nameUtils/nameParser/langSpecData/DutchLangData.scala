package org.ditw.nameUtils.nameParser.langSpecData

/**
  * Created by dev on 2017-08-25.
  */
private[nameParser]
object DutchLangData {
  /// https://en.wikipedia.org/wiki/Tussenvoegsel
  // aan (at)
  // bij (near)
  // de (the, but "de" can also be French and Spanish for "of".)
  // den, der, d' (of the)
  // het, 't (the)
  // in (in)
  // onder (under, below)
  // op (on, at)
  // over (over, beyond)
  // 's (of the, from) (genitive case)
  // te, ten, ter (at)
  // tot (till)
  // uit, uijt (from, out of) (The uij spelling is Old Dutch)
  // van, (from)
  // voor (to)

  // aan de, aan den, aan der, aan het, aan 't
  // bij de, bij den, bij het, bij 't
  // boven d'
  // in de, in den, in der, in het, in 't
  // onder de, onder den, onder het, onder 't
  // over de, over den, over het, over 't
  // op de, op den, op der, op het, op 't, op ten
  // van de, van den, van der, van het, van 't, van ter
  // uit de, uit den, uit het, uit 't, uit ten
  // uijt de, uijt den, uijt het, uijt 't, uijt ten (The uij spelling is Old Dutch)
  // ver (a contraction of van der)
  // voor de, voor den, voor in 't
  private[nameParser] object PrefixUnits {
    val aan = "aan"
    val bij = "bij"
    val de = "de"
    val den = "den"
    val der = "der"
    val die = "die"
    val d_appos = "d'"
    val het = "het"
    val appos_t = "'t"
    val in = "in"
    val onder = "onder"
    val op = "op"
    val over = "over"
    val appos_s = "'s"
    val te = "te"
    val ten = "ten"
    val ter = "ter"
    val tot = "tot"
    val uit = "uit"
    val uijt = "uijt"
    val van = "van"
    val voor = "voor"
    val ver = "ver"
    val boven = "boven"
  }

  import PrefixUnits._
  import org.ditw.nameUtils.nameParser.utils.NamePartHelpers._
  private val _NamePart2_DT1 = IndexedSeq(de, den, der, het, appos_t)
  private val _NamePart2_DT2 = IndexedSeq(de, den, het, appos_t)
  private[nameParser] val DutchSortedPrefixes = sortStringSeq(
    Seq(
      IndexedSeq(aan), // -> IndexedSeq(aan),
      IndexedSeq(bij),
      IndexedSeq(de),
      IndexedSeq(den),
      IndexedSeq(der),
      IndexedSeq(d_appos),
      IndexedSeq(het),
      IndexedSeq(appos_t),
      IndexedSeq(in),
      IndexedSeq(onder),
      IndexedSeq(op),
      IndexedSeq(over),
      IndexedSeq(appos_s),
      IndexedSeq(te),
      IndexedSeq(ten),
      IndexedSeq(ter),
      IndexedSeq(tot),
      IndexedSeq(uit),
      IndexedSeq(uijt),
      IndexedSeq(van),
      IndexedSeq(voor),
      IndexedSeq(boven, d_appos),
      IndexedSeq(op, ten),
      IndexedSeq(van, ter),
      IndexedSeq(uit, ten),
      IndexedSeq(uijt, ten),
      IndexedSeq(ver),
      IndexedSeq(voor, de),
      IndexedSeq(voor, den),
      IndexedSeq(voor, in, appos_t)
    ) ++
      toPairSeq(aan, _NamePart2_DT1) ++
      toPairSeq(bij, _NamePart2_DT2) ++
      Seq(
        IndexedSeq(de, die),
        IndexedSeq(de, die, "le"),
        IndexedSeq(de, "l'"),
        // hispanic? IndexedSeq(de, "las"),
        IndexedSeq(de, "le"),
        IndexedSeq(de, van, der)
      ) ++
      toPairSeq(in, _NamePart2_DT1) ++
      toPairSeq(onder, _NamePart2_DT2) ++
      toPairSeq(over, _NamePart2_DT2) ++
      toPairSeq(op, _NamePart2_DT1) ++
      toPairSeq(van, _NamePart2_DT1) ++
      toPairSeq(uit, _NamePart2_DT2) ++
      toPairSeq(uijt, _NamePart2_DT2)
  )


}
