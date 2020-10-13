package cn.common.tools.test

import cn.common.tools.common.Helper
import cn.common.tools.redis.{RedisClient, RedisCluster}
import cn.common.tools.utils.LoggerLevels
import org.apache.commons.lang3.StringUtils
import org.apache.spark.SparkContext
import org.apache.spark.sql.SparkSession
import redis.clients.jedis.{Jedis, JedisCluster, JedisPool, JedisPoolConfig}

import scala.util.matching.Regex

/**
  * Created by wpq on 2020/6/15.
  */
object SparkTest {
  def main(args: Array[String]) {
    /*LoggerLevels.setStreamingLogLevels()
    val sc = Helper.sparkContext
    val baseRdd = sc.textFile("D:\\avc\\临时工作支持\\唯品会\\cc\\cc_vv.txt").repartition(1000)
    baseRdd.foreachPartition(items => {
      val jedis: JedisCluster = RedisCluster.getClient
      items.foreach(row => {
        val fields = StringUtils.splitPreserveAllTokens(row, ",")
        val key = fields(0)
        val value = fields(1)
        jedis.set(key, value)
      })
    })
    sc.stop()*/

    val pattern = "(?i)\\Qas\\E(?![_$\\p{javaJavaIdentifierPart}])".r
    val str = "1t"

    println(pattern findFirstIn str)
  }
}
