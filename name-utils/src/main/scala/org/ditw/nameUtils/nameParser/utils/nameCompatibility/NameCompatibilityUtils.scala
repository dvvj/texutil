package org.ditw.nameUtils.nameParser.utils.nameCompatibility

import java.util.regex.Pattern

import org.ditw.nameUtils.nameparser.ParsedPerson
import org.ditw.nameUtils.namerules.AuthorNameUtils
/**
  * Created by dev on 2017-09-11.
  */
object NameCompatibilityUtils extends Serializable {

  type NameCompatibilityChecker = (Array[String], Array[String]) => Boolean
  type SignatureCompatibilityChecker = (String, String, Boolean) => Boolean
  type NamespaceGenerator = Array[String] => Option[String]

//  trait TChecker4Lang {
//    val lang:LanguageEnum
//    protected val nameCompatibleChecker:NameCompatibilityChecker
//    def isNameCompatible(n1:IndexedSeq[String], n2:IndexedSeq[String]):Boolean =
//      nameCompatibleChecker(n1, n2)
//    protected val clusterNameCompatibleChecker:NameCompatibilityChecker
//    def isClusterNameCompatible(n1:IndexedSeq[String], n2:IndexedSeq[String]):Boolean =
//      clusterNameCompatibleChecker(n1, n2)
//
//  }

  import NameComponentComparors._
  import org.ditw.nameUtils.nameParser.ParserHelpers._

  private[nameParser] val SpaceSplitterPtn = Pattern.compile("""\s+""")
  private[nameParser] def checkNameCompatible(
    n1:Array[String], n2:Array[String],
    conf2CheckSig:NameComparorConf=>Boolean,
    matchSigOrder:Boolean // by default signature matching does not require matching in order (old name parsing logic)
  ):Boolean = {
    //checkSizes(n1, n2)

//    if (useNewParser) {
      val (langCode1, sig1) = splitSignature(n1.head)
      val (langCode2, sig2) = splitSignature(n2.head)
      if (langCode1 != langCode2) false
      else {
        val cmprConf = getNameComparorByLangCode(langCode1)
        val checkSig = conf2CheckSig(cmprConf)
        if (checkSig && !cmprConf.sigCompatibilityChecker(sig1, sig2, matchSigOrder)) false
        else {
          val firstNames1 = n1.tail.flatMap(splitTrim(SpaceSplitterPtn, _))
          val firstNames2 = n2.tail.flatMap(splitTrim(SpaceSplitterPtn, _))
          cmprConf.compatibilityChecker(firstNames1, firstNames2)
        }
      }
//    }
//    else {
//      val fn1 = n1(1)
//      val fn2 = n2(1)
//      _oldParser.isNameCompatible(fn1, fn2)
//    }

  }
  def checkNameCompatibleJ(
    n1:Array[String], n2:Array[String],
    matchSigOrder:Boolean
  ):Boolean = {
    checkNameCompatible(n1, n2, _.checkSig4NameCompatibility, matchSigOrder)
  }


  def checkSigCompatible(s1:String, s2:String, matchOrder:Boolean):Boolean = {
    val (langCode1, sig1) = splitSignature(s1)
    val (langCode2, sig2) = splitSignature(s2)
    checkSigCompatibleLc(langCode1, sig1, langCode2, sig2, matchOrder)
  }

  def checkSigCompatibleLc(
                            langCode1:Option[String],
                            sig1:String,
                            langCode2:Option[String],
                            sig2:String,
                            matchOrder:Boolean
                          ):Boolean = {
    if (langCode1 != langCode2) false
    else {
      val cmprConf = getNameComparorByLangCode(langCode1)
      cmprConf.sigCompatibilityChecker(sig1, sig2, matchOrder)
    }
  }

  private val firstNameSignatureSeparator = "||"
  def encodeFirstNameSignature(firstName:String, signature:String):String = firstName+firstNameSignatureSeparator+signature
  private def decodeFirstNameSignature(firstNameSig:String):Array[String] = {
    val idx = firstNameSig.indexOf(firstNameSignatureSeparator)
    // assuming always well-formated, therefore no checking
    val firstName = firstNameSig.substring(0, idx)
    val sig = firstNameSig.substring(idx+firstNameSignatureSeparator.length)
    Array(sig, firstName)
  }

  def checkClusterNameCompatibleJ_CheckSigIgnoreOrder(n1:Array[String], n2:Array[String]):Boolean = {
    checkClusterNameCompatibleJ(n1, n2, false, false)
  }

  def checkClusterNameCompatibleJ(
    n1:Array[String], n2:Array[String],
    ignoreSignature:Boolean,
    matchSigOrder:Boolean
  ):Boolean = {
    if (ignoreSignature) checkNameCompatible(n1, n2, _ => false, matchSigOrder)
    else checkNameCompatible(n1, n2, _.checkSig4ClusterNameCompatibility, matchSigOrder)
  }

  private val DoNotCheckSignatureOrder = false
  private val EmptyStr = ""
  def checkClusterNameCompatibleJ(
    fnsig1:String,
    parsedPerson:ParsedPerson
  ):Boolean = {
    val n1 = decodeFirstNameSignature(fnsig1)

    val n2 = Array(parsedPerson.signature, parsedPerson.firstName)

    checkClusterNameCompatibleJ(n1, n2, true, DoNotCheckSignatureOrder)

  }

  def checkClusterNameCompatibleJ(
    pp1:ParsedPerson, pp2:ParsedPerson,
    matchSigOrder:Boolean
  ):Boolean = {

    val n1 = Array(pp1.signature, pp1.firstName)
    val n2 = Array(pp2.signature, pp2.firstName)

    checkClusterNameCompatibleJ(n1, n2, false, matchSigOrder)

  }

  private[nameParser] def genNamespace(parts:Array[String]):Option[String] = {

      val sig = parts.head
      val nameParts = parts.tail

      val (langCode, s) = splitSignature(sig)
      val cmprConf = getNameComparorByLangCode(langCode)
      cmprConf.namespaceGenerator(nameParts)
//    }
//    else {
//      if (parts.length < 3) None
//      else {
//        val lastName = parts(1)
//        val firstName = parts(2)
//        Option(AuthorNameUtils.getNamespace(firstName, lastName))
//      }
//    }
  }

  import AuthorNameUtils._
  def cleanJ(namePart:String):String = {
//    if (useNewParser) {
      if (namePart != null) asciify(namePart)
      else EmptyStr
//    }
//    else clean(namePart)
  }

  def genNamespaceJ(signature:String, lastName:String, firstName:String):String = {
    val parts = Array(signature, lastName) ++ splitTrim(SpaceSplitterPtn, firstName)
    val ns = genNamespace(parts)
    if (ns.nonEmpty) ns.get
    else AuthorNameUtils.InvalidNameSpace
  }

  def genNamespaceFromJ(parsedPerson:ParsedPerson):String = {
    genNamespaceJ(parsedPerson.signature, parsedPerson.lastName, parsedPerson.firstName)
  }

  private def checkSizes(n1:Array[String], n2:Array[String]):Unit = {
    if (n1.length != n2.length)
      throw new IllegalArgumentException(s"Expecting same sizes to compare, but actual values: $n1 vs. $n2")
  }

  def splitSignature(sig:String):(Option[String], String) = {
    val idx = sig.indexOf(':')
    if (idx > 0) {
      val langCode = sig.substring(0, idx)
      val rs = sig.substring(idx+1)
      Option(langCode) -> rs
    }
    else None -> sig
  }

  def isNamespaceChinese(ns:String):Boolean = ns.endsWith(NamespaceSuffix_Chinese)
  def isNamespaceJapanese(ns:String):Boolean = ns.endsWith(NamespaceSuffix_Japanese)

  private[nameParser] def genNamespace_FullFirstName(parts:Array[String]):Option[String] = {

    val sig = parts.head
    val nameParts = parts.tail

    val (langCode, s) = splitSignature(sig)
    val cmprConf = getNameComparorByLangCode(langCode)
    cmprConf.namespaceGenerator_FullFirstName(nameParts)

  }

  def genNamespaceJ_FullFirstName(signature:String, lastName:String, firstName:String):String = {
    val parts = Array(signature, lastName) ++ splitTrim(SpaceSplitterPtn, firstName)
    val ns = genNamespace_FullFirstName(parts)
    if (ns.nonEmpty) ns.get
    else AuthorNameUtils.InvalidNameSpace
  }

  def fixSignature(firstName:String, lastName:String, sig:String, nameSource:String):String = {
    val (langCode, s) = splitSignature(sig)
    val cmprConf = getNameComparorByLangCode(langCode)
    cmprConf.sigFixer(firstName, lastName, sig, nameSource)
  }

  def checkInitialOnlyNamespace(sig:String, firstNames:String):Boolean = {
    val (langCode, s) = splitSignature(sig)

    val cmprConf = getNameComparorByLangCode(langCode)
    cmprConf.initialOnlyNamespaceChecker(firstNames)
  }
}
