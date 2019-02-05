package org.ditw.es
import java.net.InetAddress

import org.elasticsearch.action.search.SearchType
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.index.query.QueryBuilders
import org.elasticsearch.transport.client.PreBuiltTransportClient

object SearchTest extends App {

  val settings = Settings.builder().put(
    "cluster.name", "my-app"
  ).build()

  val clt = new PreBuiltTransportClient(settings)
  clt.addTransportAddress(
    new TransportAddress(
      InetAddress.getByName("localhost"), 9300
    )
  )

  val resp = clt.prepareSearch("td_idx")
    .setSearchType(SearchType.DEFAULT)
    .setQuery(QueryBuilders.matchAllQuery())
    .setSize(15)
    .execute()
    .actionGet()

  val hits = resp.getHits.getHits
  println(s"${hits.size}")

  val s = resp.toString
  println(s)

}
