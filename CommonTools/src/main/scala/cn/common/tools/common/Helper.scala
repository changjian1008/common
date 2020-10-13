package cn.common.tools.common

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.hadoop.hbase.HBaseConfiguration
import org.apache.hadoop.hbase.mapred.TableOutputFormat
import org.apache.hadoop.mapred.JobConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.hive.HiveContext
import org.apache.spark.{SparkConf, SparkContext}

object Helper {
  //默认配置文件读取
  lazy val config: Config = ConfigFactory.load()

  //初始化Spark配置
  lazy val sparkConf = new SparkConf()
    .setIfMissing("spark.master", config.getString("spark.master"))
    .setIfMissing("spark.app.name", config.getString("spark.app.name"))
    .setIfMissing("es.nodes", config.getString("es.nodes"))
    .setIfMissing("es.port", config.getString("es.port"))
    .setIfMissing("es.index.auto.create", "true")
    .setIfMissing("es.nodes.wan.only", "true")

  //获取上下文对象
  def getNewContext(): SparkContext = {
    sparkConf.set("spark.streaming.backpressure.enabled", "true")
    sparkConf.set("spark.sql.parquet.compression.codec", "snappy")
    sparkConf.set("spark.sql.parquet.mergeSchema", "true")
    sparkConf.set("spark.sql.parquet.binaryAsString", "true")
    sparkConf.set("spark.shuffle.consolidateFiles", "true")
    sparkConf.set("spark.driver.maxResultSize", "10g")
    new SparkContext(sparkConf)
  }

  //初始化SparkContext
  lazy val sparkContext: SparkContext = getNewContext()

  def getHiveContext(): HiveContext = {
    val sqlContext: HiveContext = new HiveContext(sparkContext)
    sqlContext.sql("SET hive.exec.dynamic.partition=true")
    sqlContext.sql("set hive.execution.engine=spark")
    sqlContext.sql("SET hive.exec.dynamic.partition.mode=nonstrict")
    sqlContext.sql("set hive.mapred.supports.subdirectories=true")
    sqlContext.sql("set mapreduce.input.fileinputformat.input.dir.recursive=true")
    sqlContext.sql("set hive.merge.mapfiles=true")

    sqlContext
  }

  def getSparkSession = {
    SparkSession.builder()
      .config(sparkConf)
      .enableHiveSupport()
      .getOrCreate()
  }

  //初始化SparkContext
  lazy val hiveContext: HiveContext = getHiveContext()

  //命令行参数解析
  def parseOptions(args: Array[String], index: Int, defaultValue: String): String = {
    if (args.length > index) args(index) else defaultValue
  }

  def mysqlConf = {
    val prop = new java.util.Properties()
    prop.put("user", config.getString("mysql.user"))
    prop.put("password",config.getString("mysql.password"))
    prop.put("driver", config.getString("mysql.driver"))
    prop
  }

  def hiveConf = {
    val prop = new java.util.Properties()
    prop.put("user", "")
    prop.put("password", "")
    prop.put("driver", "org.apache.hive.jdbc.HiveDriver")
    prop
  }

  def hbaseConf(tablename: String) = {
    val hbaseConf = HBaseConfiguration.create()
    hbaseConf.set("hbase.zookeeper.quorum", config.getString("zookeeper.quorum"))
    hbaseConf.set("hbase.zookeeper.property.clientPort", config.getString("zookeeper.port"))
    hbaseConf.set(TableOutputFormat.OUTPUT_TABLE, tablename)
    hbaseConf
  }

  /**
    * 初始化job
    */
  def getJobConf(tablename: String): JobConf = {
    val jobConf = new JobConf(hbaseConf(tablename))
    jobConf.setOutputFormat(classOf[TableOutputFormat])
    jobConf
  }

  def mysqlUrl(islocal:Boolean) = {
    if (islocal) {
      config.getString("mysql.url.local")
    }else {
      config.getString("mysql.url.remote")
    }
  }
}
