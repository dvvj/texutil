package org.ditw.es.utils
import java.net.InetAddress

import org.elasticsearch.action.search.SearchResponse
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings
import org.elasticsearch.common.transport.TransportAddress
import org.elasticsearch.index.query.QueryBuilder
import org.elasticsearch.transport.client.PreBuiltTransportClient

object EsUtils extends Serializable {

  def localClient(clusterName:String):TransportClient = {
    val settings = Settings.builder().put(
      "cluster.name", "my-app"
    ).build()

    val clt = new PreBuiltTransportClient(settings)
    clt.addTransportAddress(
      new TransportAddress(
        InetAddress.getByName("localhost"), 9300
      )
    )
    clt
  }

  def runSearch(clt: TransportClient, index:String, q: QueryBuilder, sz:Int):SearchResponse = {
    val resp = clt.prepareSearch(index)
      .setQuery(q)
      .setSize(sz)
      .execute()
      .actionGet()
    resp
  }

}
