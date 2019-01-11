package org.ditw.nameUtils.nameParser.utils.nameCompatibility

import org.ditw.nameUtils.SimplifyCharset
import NameCompatibilityUtils.{NameCompatibilityChecker, NamespaceGenerator, SignatureCompatibilityChecker}
import org.ditw.nameUtils.SimplifyCharset
import org.ditw.nameUtils.nameParser.langSpecData.jpn.HanaAndLastName
import org.ditw.nameUtils.namerules.AuthorNameUtils

/**
  * Created by dev on 2017-09-12.
  */
object NameComponentComparors {
  private def nameCompComparor_SameLen(
    comp1:Array[String], comp2:Array[String]
  ):Boolean = {

    if (comp1.length != comp2.length) false
    else
      comp1.indices.forall { idx =>
        val fn1 = comp1(idx).toLowerCase
        val fn2 = comp2(idx).toLowerCase
        if ((fn1.length == 1 || fn2.length == 1) && fn1.head == fn2.head) true
        else fn1 == fn2
      }
  }

  private def chineseNameCompComparor(
    comp1:Array[String], comp2:Array[String]
  ):Boolean = nameCompComparor_SameLen(comp1, comp2)


  private def japaneseNameCompComparor(
    comp1:Array[String], comp2:Array[String]
  ):Boolean = nameCompComparor_SameLen(comp1, comp2)

  private def generalNameCompComparor(
    comp1:Array[String], comp2:Array[String]
  ):Boolean = {
    var compatible = true
    val minSize = Math.min(comp1.length, comp2.length)
    var idx = 0
    while (compatible && idx < minSize) {
      val c1 = AuthorNameUtils.asciify(comp1(idx).toLowerCase)
      val c2 = AuthorNameUtils.asciify(comp2(idx).toLowerCase)

      if (c1.length == 1 || c2.length == 1) {
        compatible = c1.head == c2.head
      }
      else {
        compatible = c1 == c2
      }
      idx = idx+1
    }
    compatible
  }

  import org.ditw.nameUtils.nameParser.ParserHelpers._
  private def checkNameCompatibleOld(
    n1:Array[String], n2:Array[String]
  ):Boolean = {
    val fn1 = combineNameComponents(n1)
    val fn2 = combineNameComponents(n2)
//    if (n1.length != 1 || n2.length != 1)
//      throw new IllegalArgumentException(s"Expecting first names only, but actual value: ${n1.mkString(" ")} vs ${n2.mkString(" ")}")
    _oldParser.isNameCompatible(fn1, fn2)
  }
  // old implementation as default
  //private val defaultComparor:NameCompatibilityChecker = checkNameCompatibleOld

  private def checkValidNameParts(parts:Array[String]):Boolean = {
    parts.length >= 2 &&
      parts(0) != null && !parts(0).isEmpty &&
      parts(1) != null && !parts(1).isEmpty
  }
  import AuthorNameUtils._

  private val NamespaceSeparator = "|"
  private[nameParser] val NamespaceSuffix_Chinese = ":zho"
  private[nameParser] def chineseNamespaceGenerator(parts:Array[String]):Option[String] = {
    if (!checkValidNameParts(parts)) None
    else {
      val lastName = parts.head.toLowerCase
      val firstNameInits = parts.tail.map(_.head.toLower).mkString(NamespaceSeparator)
      Option(s"$lastName$NamespaceSeparator$firstNameInits$NamespaceSuffix_Chinese")
    }
  }

  private val HispanicLastNameSeparator = "/"
  private[nameParser] val NamespaceSuffix_Hispanic = ":hisp"
  private val hispanicNamespaceLastNameIgnored = Set(
    "de", "la", "da", "do", "dos", "das", "las", "los"
  )
  private[nameParser] def hispanicNamespaceGenerator(parts:Array[String]):Option[String] = {
    if (!checkValidNameParts(parts)) None
    else {
      val lastName = parts.head.toLowerCase
      val lastNameComps = splitComponents(lastName).map(SimplifyCharset.normalizeAndAsciify)
        .filter(p => !hispanicNamespaceLastNameIgnored.contains(p))
      val lastNamePart = lastNameComps.mkString(HispanicLastNameSeparator)
      val firstNameInits = parts.tail.head.head.toLower //.map(_.head.toLower).mkString(NamespaceSeparator)
      Option(s"$lastNamePart$NamespaceSeparator$firstNameInits$NamespaceSuffix_Hispanic")
    }
  }


  val NamespaceSuffix_Japanese = ":jpn"
  private[nameParser] def japaneseNamespaceGenerator(parts:Array[String]):Option[String] = {
    if (!checkValidNameParts(parts)) None
    else {
      val normParts = parts.map(p => HanaAndLastName.normalize(p).toLowerCase)
      val lastName = normParts.head
      val firstNamePart = normParts.tail.mkString(NamespaceSeparator)
      Option(s"$lastName$NamespaceSeparator$firstNamePart$NamespaceSuffix_Japanese")
    }
  }
  private[nameParser] def generalNamespaceGenerator(parts:Array[String]):Option[String] = {
    if (!checkValidNameParts(parts)) None
    else {
      val lastName = parts.head.toLowerCase
      val firstNameInit = parts(1).head.toLower
      val ns = asciify(s"$lastName$NamespaceSeparator$firstNameInit")
      Option(ns)
    }
  }

  private[nameParser] def oldNamespaceGenerator(parts:Array[String]):Option[String] = {
    if (!checkValidNameParts(parts)) None
    else {
      val lastName = parts.head.toLowerCase
      val firstName = parts(1)
      val r = getNamespace(firstName, lastName)
      if (r == InvalidNameSpace) None
      else Option(r)
      //Option(s"$lastName$NamespaceSeparator$firstNameInit")
    }
  }

  private[nameParser] def oldSignatureCompatibilityChecker(s1:String, s2:String, matchOrder:Boolean):Boolean = {
    AuthorNameUtils.signatureMatch(s1, s2, matchOrder)
  }

  private[nameParser] def generalSignatureCompatibilityChecker(s1:String, s2:String, matchOrder:Boolean):Boolean = {
    val min = Math.min(s1.length, s2.length)
    if (min > 0) {
      //(0 until min).forall(idx => s1(idx) == s2(idx))
      val ss1 = asciify(s1.substring(0, min).toLowerCase)
      val ss2 = asciify(s2.substring(0, min).toLowerCase)
      ss1 == ss2
    }
    else false
  }
  private[nameParser] def signatureCompatibilityChecker_SameLen(s1:String, s2:String):Boolean = {
    if (s1.length != s2.length) false
    else {
      if (s1.length > 0) s1 == s2 //(0 until s1.length).forall(idx => s1(idx) == s2(idx))
      else false
    }
  }

  private[nameParser] def chineseSignatureCompatibilityChecker(
    s1:String, s2:String, matchOrder:Boolean
  ):Boolean = signatureCompatibilityChecker_SameLen(s1, s2)

  private[nameParser] def japaneseSignatureCompatibilityChecker(
    s1:String, s2:String, matchOrder:Boolean
  ):Boolean = signatureCompatibilityChecker_SameLen(s1, s2)

  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LanguageEnum._

  type InitialOnlyNamespaceChecker = String => Boolean
  type SignatureFixer = (String, String, String, String) => String

  private[nameParser] class NameComparorConf(
    val lang:Option[LanguageEnum],
    val namespaceGenerator:NamespaceGenerator,
    val namespaceGenerator_FullFirstName:NamespaceGenerator,
    val checkSig4NameCompatibility:Boolean,
    val compatibilityChecker:NameCompatibilityChecker,
    //    val checkClusterNameCompatible:NameCompatibilityChecker,
    val checkSig4ClusterNameCompatibility:Boolean,
    val sigCompatibilityChecker:SignatureCompatibilityChecker,
    val initialOnlyNamespaceChecker:InitialOnlyNamespaceChecker,
    val sigFixer:SignatureFixer
  )

  private[nameParser] def oldNamespaceGenerator_FullFirstName(parts:Array[String]):Option[String] = {
    if (!checkValidNameParts(parts)) None
    else {
      val lastName = parts.head.toLowerCase
      val firstName = parts(1)
      val r = getNamespace_FullFirstName(firstName, lastName)
      if (r == InvalidNameSpace) None
      else Option(r)
      //Option(s"$lastName$NamespaceSeparator$firstNameInit")
    }
  }

  private[nameParser] def chineseNamespaceGenerator_FullFirstName(parts:Array[String]):Option[String] = {
    if (!checkValidNameParts(parts)) None
    else {
      val lastName = parts.head.toLowerCase
      val firstNameInits = parts.tail.map(_.toLowerCase).mkString(NamespaceSeparator)
      Option(s"$lastName$NamespaceSeparator$firstNameInits$NamespaceSuffix_Chinese")
    }
  }

  private[nameParser] def oldInitialOnlyNamespaceChecker(firstName:String):Boolean = {
    firstName.length == 1
  }
  private[nameParser] def initialOnlyNamespaceChecker_MultiParts(firstName:String):Boolean = {
    val fnParts = splitBySpace(firstName)
    fnParts.forall(_.length == 1)
  }
  private[nameParser] def chineseInitialOnlyNamespaceChecker(firstName:String):Boolean =
    initialOnlyNamespaceChecker_MultiParts(firstName)
  private[nameParser] def japaneseInitialOnlyNamespaceChecker(firstName:String):Boolean =
    initialOnlyNamespaceChecker_MultiParts(firstName)
  private[nameParser] def generalInitialOnlyNamespaceChecker(firstName:String):Boolean = {
    firstName.length == 1
  }

  private[nameParser] def oldSigFixer(
    firstName:String,
    lastName:String,
    currSig:String,
    nameSource:String
  ):String = {
    if (currSig.length > 1) currSig // only deal with single signature for now
    else {
      var nameSourceRem = nameSource.toLowerCase.trim

      val firstNameIdx = nameSourceRem.indexOf(firstName.toLowerCase)
      val firstNameFound = firstNameIdx >= 0
      if (firstNameIdx == 0) {
        // most common case
        nameSourceRem = nameSourceRem.substring(firstName.length).trim
      }
      else if (firstNameIdx > 0) {
        // first name does not appear first, e.g. "H Josh ..." Josh will be treated as the first name
        val part1 = nameSourceRem.substring(0, firstNameIdx).trim
        val part2 = nameSourceRem.substring(firstNameIdx + firstName.length).trim
        nameSourceRem = s"$part1 $part2"
      }
      else {
        // first name string not found, possible cause, there are '-'s in the first name, e.g.
        //   "sun-ha cheon" first name becomes 'sunha' todo: this should be fixed

      }

      val lastNameIdx = nameSourceRem.lastIndexOf(lastName.toLowerCase)
      if (lastNameIdx >= 0) {
        nameSourceRem = nameSourceRem.substring(0, lastNameIdx).trim
      }

      // note: this should be done after last name is removed (otherwise, '-' in the last name
      //   will also be replaced
      if (!firstNameFound)
        nameSourceRem = nameSourceRem.replace('-', ' ')

      if (nameSourceRem.nonEmpty) {
        val exSigs = splitBySpace(nameSourceRem).map(_.charAt(0)).mkString
        if (firstNameFound) // append initials
          currSig + exSigs
        else // ignore old signature
          exSigs
      }
      else currSig
    }
  }

  private[nameParser] def sigFixer_NoFix(
    firstName:String,
    lastName:String,
    currSig:String,
    nameSource:String
  ):String = currSig

  private val _defaultComparorConf = new NameComparorConf(
    None, oldNamespaceGenerator, oldNamespaceGenerator_FullFirstName,
    false, checkNameCompatibleOld,
    true, oldSignatureCompatibilityChecker,
    oldInitialOnlyNamespaceChecker,
    oldSigFixer
  )

  private val _comparorConfs = List(
    new NameComparorConf(
      Option(Chinese), chineseNamespaceGenerator, chineseNamespaceGenerator_FullFirstName,
      true, chineseNameCompComparor,
      true, chineseSignatureCompatibilityChecker,
      chineseInitialOnlyNamespaceChecker,
      sigFixer_NoFix
    ),
    new NameComparorConf(
      Option(Dutch), generalNamespaceGenerator, oldNamespaceGenerator_FullFirstName,
      false, generalNameCompComparor,
      true, generalSignatureCompatibilityChecker,
      generalInitialOnlyNamespaceChecker,
      sigFixer_NoFix
    ),
    new NameComparorConf(
      Option(Japanese), japaneseNamespaceGenerator, japaneseNamespaceGenerator, // already using full first-name
      true, japaneseNameCompComparor,
      true, japaneseSignatureCompatibilityChecker,
      japaneseInitialOnlyNamespaceChecker,
      sigFixer_NoFix
    )
  )

  private[nameParser] val _nameCompComparorMap = _comparorConfs.map(c => c.lang.get -> c).toMap

  import org.ditw.nameUtils.nameParser.utils.NameLanguages.LangCodeReverseMap

  private[nameParser] def getNameComparorByLangCode(langCode:Option[String]):NameComparorConf = {
    if (langCode.nonEmpty) {
      val lang = LangCodeReverseMap.get(langCode.get)
      if (lang.nonEmpty) _nameCompComparorMap.getOrElse(lang.get, _defaultComparorConf)
      else _defaultComparorConf
    }
    else _defaultComparorConf
  }
}
