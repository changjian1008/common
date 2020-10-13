package cn.common.tools.utils

import org.apache.log4j.{Level, Logger}

/**
  * Created by wpq on 2018/6/16.
  * 仅显示错误信息
  */
object LoggerLevels {

  def setStreamingLogLevels() {

    Logger.getLogger("org.apache.spark").setLevel(Level.ERROR)

    Logger.getLogger("org.apache.hadoop").setLevel(Level.ERROR)

    Logger.getLogger("org.apache.zookeeper").setLevel(Level.WARN)

    Logger.getLogger("org.apache.hive").setLevel(Level.WARN)
  }

}