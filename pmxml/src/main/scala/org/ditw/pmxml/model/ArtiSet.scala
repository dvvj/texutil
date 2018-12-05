package org.ditw.pmxml.model

import com.lucidchart.open.xtract.{ XmlReader, __ }
import com.lucidchart.open.xtract.XmlReader._

import Constants.XmlTags._
import cats.syntax.all._


case class ArtiSet(artis:Seq[Arti]) {

}

object ArtiSet extends Serializable {
  val EmptyArtiSet = ArtiSet(Seq())

  implicit val reader:XmlReader[ArtiSet] = (
    (__ \ PubmedArticle).read(seq[Arti]).map(apply)
  )
}