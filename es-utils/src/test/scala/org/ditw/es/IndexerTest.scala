package org.ditw.es

import java.io.FileInputStream
import java.nio.charset.StandardCharsets

import org.apache.commons.io.IOUtils
import org.elasticsearch.client.{RestClient, RestHighLevelClient}
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.common.xcontent.XContentType
import org.elasticsearch.rest.RestStatus

object IndexerTest extends App {


  val restClt = new RestHighLevelClient(
    RestClient.builder(
      new HttpHost("127.0.0.1", 9200, "http")
    )
  )

  import collection.JavaConverters._
  val strm = new FileInputStream("/media/sf_vmshare/pmjs/ssra/part-00000")
  val lines = IOUtils.readLines(strm, StandardCharsets.UTF_8).asScala.toArray

  strm.close()

  //val ls = lines.asScala.take(5)
  lines.indices.foreach {idx =>
    val l = lines(idx)
    val idxReq = new IndexRequest("td_idx", "typ", idx.toString)
      .source(l, XContentType.JSON)
    val resp = restClt.index(idxReq)
    if (resp.status() != RestStatus.CREATED)
      println("error?")
    else
      println(s"Indexed line $idx")
  }

  restClt.close()

//  val idxReq =
//  restClt.index(
//
//  )

}
