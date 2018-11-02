package org.ditw.textSeg
import org.ditw.common.{Dict, InputHelpers}
import org.ditw.tknr.{Tokenizers, Trimmers}
import org.ditw.tknr.Tokenizers.{TTokenizer, TokenizerSettings}

object Settings extends Serializable {

  private[textSeg] val _Dict: Dict =
    InputHelpers.loadDict(
      "0123456789".map(_.toString)
    )

  private[textSeg] val _PunctChars = ".,;:()[]\""
  private[textSeg] val _AffIndexChars =
    """╫╪^?ζΘΦΨχΣΠξΔǁ«»¿×®°±¹²³ª©*†‡§¶∥‖║#△■●□⊥∇⁎€⁴№∞∧∫∮≠⊕⊗⊗⊗⊞⋈⌋⑊┘┼┼□▰▲▼▽◆◇◊○◐★☆⚲⦀⧓⿿☼§𝕃�|£¤¥⟁◑◪⧖⧧⧨⧩⧫⬢⬡⬠⬟⧳̂⊗"""

  implicit val TknrTextSeg:TTokenizer = Tokenizers.load(
    TokenizerSettings(
      "\\n+",
      "[\\h]+",
      List(),
      Trimmers.byChars(
        _AffIndexChars + _PunctChars
      )
    )
  )
}
