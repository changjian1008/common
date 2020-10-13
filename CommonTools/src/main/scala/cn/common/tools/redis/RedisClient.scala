package cn.common.tools.redis

import com.typesafe.config.{Config, ConfigFactory}
import org.apache.commons.pool2.impl.GenericObjectPoolConfig
import redis.clients.jedis.{JedisCluster, JedisPool}

object RedisClient extends Serializable {
  val config: Config = ConfigFactory.load()
  val redisHost = config.getString("redisHost")
  val redisPort = config.getInt("redisPort")
  val redisTimeout = config.getInt("redisTimeout")

  lazy val pool = new JedisPool(new GenericObjectPoolConfig(), redisHost, redisPort, redisTimeout)

  lazy val hook = new Thread {
    override def run = {
      println("Execute hook thread: " + this)
      pool.destroy()
    }
  }
  //钩子函数，系统异常结束，关闭redis连接
  sys.addShutdownHook(hook.run)

  /***
    *  //保存到Redis中
          val jedis = RedisClient.pool.getResource
          jedis.select(dbIndex)
          //每个商品销售额累加
          jedis.hincrBy(orderTotalKey, x._1, x._3)
          //上一分钟第每个商品销售额
          jedis.hset(oneMinTotalKey, x._1.toString, x._3.toString)
          //总销售额累加
          jedis.incrBy(totalKey, x._3)
          RedisClient.pool.returnResource(jedis)

          /////////////////////
    *   def main(args: Array[String]): Unit = {
    val dbIndex = 0

    val jedis = RedisClient.pool.getResource
    jedis.select(dbIndex)
    jedis.set("test", "1")
    println(jedis.get("test"))
    RedisClient.pool.returnResource(jedis)

  }
    */
}
