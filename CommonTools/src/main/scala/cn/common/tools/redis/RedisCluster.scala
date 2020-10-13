package cn.common.tools.redis

import java.util

import redis.clients.jedis.{HostAndPort, JedisCluster, JedisPoolConfig}

object RedisCluster extends Serializable {
  //连接配置
  lazy val config = new JedisPoolConfig
  //最大连接数
  config.setMaxTotal(200)
  //最大空闲连接数
  config.setMaxIdle(10)
  lazy val node1 = new HostAndPort("172.16.121.102", 6379)
  lazy val node2 = new HostAndPort("172.16.121.103", 6379)
  lazy val node3 = new HostAndPort("172.16.121.104", 6379)
  lazy val nodes: util.HashSet[HostAndPort] = new util.HashSet[HostAndPort]()
  nodes.add(node1)
  nodes.add(node2)
  nodes.add(node3)
  var jedisCluster: JedisCluster = null

  def getClient = {
    if (jedisCluster == null) {
      jedisCluster = new JedisCluster(nodes, config)
    }
    jedisCluster
  }
}
