package org.ditw.es
import java.net.InetAddress

import org.ditw.es.utils.EsUtils
import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.transport.client.PreBuiltTransportClient

object SearchTest extends App {

  val clt = EsUtils.localClient("my-app")

  //val q = QueryBuilders.termQuery("res.name", "nist")
  //val q = QueryBuilders.matchQuery("res.name", "nist")
//  val q = QueryBuilders.matchQuery("res.name", "nits")
//    .fuzziness(2)
//  val q = QueryBuilders.matchPhraseQuery("res.name", "University of Maryland")
//  val q = QueryBuilders.matchPhrasePrefixQuery("res.name", "University of Maryl")
  val q = QueryBuilders.multiMatchQuery("University of Maryl", "res.name", "res.seg")

  val resp = EsUtils.runSearch(clt, "td_idx", q, 15)

  val hits = resp.getHits.getHits
  println(s"${hits.size}")

  val s = resp.toString
  println(s)

}
