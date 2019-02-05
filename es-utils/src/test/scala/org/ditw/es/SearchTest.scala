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

  val resp = EsUtils.runSearch(clt, "td_idx", QueryBuilders.matchAllQuery(), 15)

  val hits = resp.getHits.getHits
  println(s"${hits.size}")

  val s = resp.toString
  println(s)

}
