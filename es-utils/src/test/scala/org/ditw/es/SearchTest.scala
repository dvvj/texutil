package org.ditw.es
import org.elasticsearch.index.query.QueryBuilders

object SearchTest extends App {


  val q = QueryBuilders.matchQuery("res.name", "NIST")
  val bq = QueryBuilders.boolQuery()
  bq.must(q)

}
