package cn.common.tools.utils

import java.sql.{DriverManager, PreparedStatement, ResultSet}

import cn.common.tools.common.Helper
import org.apache.spark.SparkContext
import org.apache.spark.sql.SaveMode
import org.apache.spark.sql.hive.HiveContext

/**
  * Created by wpq on 2018/1/11.
  * Mysql辅助工具类
  */
object MySqlUtil {
  //本地访问
  lazy final val LOCAL = true
  //远程访问
  lazy final val REMOTE = false

  def select(sql:String,islocal:Boolean) = {
    val url = Helper.mysqlUrl(islocal)
    val conn = DriverManager.getConnection(url, Helper.mysqlConf)
    val stmt = conn.createStatement()
    val query: ResultSet = stmt.executeQuery(sql)
    query
  }

  /**
    *
    * @param sql  sql语句
    * @param islocal 是否为本地数据库
    * @return
    */
  def write(sql:String,database:String,islocal:Boolean) = {
    val url = Helper.mysqlUrl(islocal)
    val conn = DriverManager.getConnection("jdbc:mysql://192.168.200.16:3306/mysql?useUnicode=true&characterEncoding=utf8&useSSL=false", Helper.mysqlConf)
    val stmt = conn.createStatement()
    val update: Int = stmt.executeUpdate(sql)
    conn.close()
    update
  }

  def writeOverwrite(sqlContext: HiveContext, sc: SparkContext, sql: String, targetTable: String, prop: java.util.Properties, url: String) {
    sqlContext.sql(sql).write
      .mode(SaveMode.Overwrite)
      .jdbc(url, targetTable, prop)
  }

  def writeAppend(sqlContext: HiveContext, sc: SparkContext, sql: String, targetTable: String, prop: java.util.Properties, url: String) {
    sqlContext.sql(sql).write
      .mode(SaveMode.Append)
      .jdbc(url, targetTable, prop)
  }

  //批量导入
  def batchWrite(sql:String,islocal:Boolean) = {
    val url = Helper.mysqlUrl(islocal)
    val conn = DriverManager.getConnection(url, Helper.mysqlConf)
    conn.setAutoCommit(false)
    val tabs: PreparedStatement = conn.prepareStatement(sql)
    (tabs,conn)
  }
}
