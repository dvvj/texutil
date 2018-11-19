package org.ditw.sparkRuns
import java.io.File

import org.apache.commons.io.FileUtils
import org.apache.spark.{SparkConf, SparkContext}

object SparkUtils {

  private def config(localRun:Boolean, appName:String, numReducer:Int) = {
    val _conf = new SparkConf()
      .setAppName(appName)
      .set("spark.hadoop.fs.s3n.impl","org.apache.hadoop.fs.s3native.NativeS3FileSystem")
      .set("spark.hadoop.fs.s3.impl","org.apache.hadoop.fs.s3.S3FileSystem")
    if (localRun) {
      _conf.set("spark.kryoserializer.buffer.mb", "1024")
      _conf.set("spark.driver.maxResultSize", "1g")
      _conf.setMaster("local[*]")
    }

    _conf.set("spark.sql.shuffle.partitions", s"$numReducer")
    _conf
  }

  def sparkContext(localRun:Boolean, appName:String = "[NO NAME]", numReducer:Int = 4) = {
    val conf = config(localRun, appName, numReducer)
    val spark = new SparkContext(conf)

    if (localRun) {
      spark.setLogLevel("ERROR")
    }

    //    if (localRun) {
    //      val credentialsProvider = new ProfileCredentialsProvider
    //      spark.hadoopConfiguration.set("fs.s3n.awsAccessKeyId", credentialsProvider.getCredentials.getAWSAccessKeyId)
    //      spark.hadoopConfiguration.set("fs.s3n.awsSecretAccessKey", credentialsProvider.getCredentials.getAWSSecretKey)
    //    }
    spark
  }

  def sparkContextLocal(appName:String = "[NO NAME]", numReducer:Int = 4) = {
    sparkContext(true, appName, numReducer)
  }

  def deleteLocal(path:String):Unit = {
    val pathFile = new File(path)
    if (pathFile.exists()) {
      FileUtils.deleteDirectory(pathFile)
    }
  }

//  def sparkSessionLocal(appName:String = "[NO NAME]", numReducer:Int = 4) = {
//    val conf = config(true, appName, numReducer)
//    SparkSession.builder.config(conf).getOrCreate()
//  }
}
