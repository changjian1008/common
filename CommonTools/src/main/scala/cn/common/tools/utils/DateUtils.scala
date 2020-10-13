package cn.common.tools.utils

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import org.apache.spark.SparkContext
import org.apache.spark.sql.hive.HiveContext
import org.joda.time.DateTime

import scala.collection.SortedMap
import scala.collection.mutable.ArrayBuffer

/**
  * Created by wpq on 2018/9/12.
  * 日期、时间工具类
  */
object DateUtils {
  lazy final val DATE_FORMAT_Hour_Minute_Second = "yyyy-MM-dd HH:mm:ss"

  lazy final val DATE_FORMAT = "yyyy-MM-dd"

  lazy final val DATE_FORMAT_yyyyMMdd = "yyyyMMdd"

  lazy final val ONE_HOUR_MILLISECONDS = 60 * 60 * 1000

  /**
    * 处理apk时间(按小时拆分时间)
    *
    * @param startTime 开始时间
    * @param endTime   结束时间
    * @return
    */
  def apkTimeByHour(currentDate: String, startTime: String, endTime: String): SortedMap[(String, String), String] = {
    var currTime = startTime
    var map: SortedMap[(String, String), String] = SortedMap()
    val nextDay = DateTime.parse(currentDate).plusDays(1).toString(DateUtils.DATE_FORMAT_Hour_Minute_Second)

    //启动时长
    var hours = 0
    //不跨天时长
    if (startTime.substring(0, 10).equals(endTime.substring(0, 10))) {
      hours = endTime.substring(11, 13).toInt - startTime.substring(11, 13).toInt
      //跨一天
    } else if ((startTime < currentDate && endTime >= currentDate && endTime < nextDay)
      || (startTime >= currentDate && startTime < nextDay && endTime >= nextDay)) {
      hours = (24 - startTime.substring(11, 13).toInt) + endTime.substring(11, 13).toInt
      //跨两天
    } else if (startTime < currentDate && endTime >= nextDay) {
      hours = (24 - startTime.substring(11, 13).toInt) + 24 + endTime.substring(11, 13).toInt
    }

    //小时为0
    if (hours == 0) {
      var dura = (date2Timestamp(endTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(startTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
      //当出现endtime为2016-11-14 00:51:37,但时长有是夸了天的，但是这个时候把startTime置为2016-11-14 00:00:00了
      //需要对时长duration做一个处理
      if (startTime.substring(11, 13).equals("00") && endTime.substring(11, 13).equals("00"))
        dura = (date2Timestamp(endTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(startTime, DATE_FORMAT_Hour_Minute_Second)) /1000
      map += ((startTime.split(" ")(0), startTime.substring(11, 13)) -> dura.toString)
      map
    } else {
      //开始时间与结束时间为同一天
      if ((startTime < currentDate && endTime < currentDate) || (startTime >= currentDate && startTime < nextDay && endTime < nextDay) || (startTime >= nextDay && endTime >= nextDay)) {
        val date = currTime.split(" ")(0)
        for (i <- 0 to hours) {
          val hour = currTime.substring(11, 13).toInt //获取当前的小时时间
          var nextTime = ""
          if (hour + 1 < 10) {
            nextTime = currTime.substring(0, 10) + " 0" + (hour + 1) + ":00:00" //个位数补0
          } else {
            nextTime = currTime.substring(0, 10) + " " + (hour + 1) + ":00:00" //得到下一小时的时间
          }
          var dura = 0l
          if (hour == endTime.substring(11, 13).toInt) {
            dura = (date2Timestamp(endTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(currTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
            map += ((date, endTime.substring(11, 13)) -> dura.toString)
          } else {
            dura = (date2Timestamp(nextTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(currTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
            map += ((date, currTime.substring(11, 13)) -> dura.toString)
          }
          currTime = nextTime
        }
        //跨一天
      } else if ((startTime < currentDate && endTime >= currentDate && endTime < nextDay) || (startTime >= currentDate && startTime < nextDay && endTime >= nextDay)) {
        //开始时间到今天凌晨的部分
        var hourCount = 23 - currTime.substring(11, 13).toInt
        var date = currTime.split(" ")(0)
        for (i <- 0 to hourCount) {
          val hour = currTime.substring(11, 13).toInt //获取当前的小时时间
          var nextTime = ""
          if (hour + 1 < 10) {
            nextTime = currTime.substring(0, 10) + " 0" + (hour + 1) + ":00:00" //个位数补0
          } else {
            nextTime = currTime.substring(0, 10) + " " + (hour + 1) + ":00:00" //得到下一小时的时间
          }
          val dura = (date2Timestamp(nextTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(currTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
          map += ((date, currTime.substring(11, 13)) -> dura.toString)
          currTime = nextTime
        }
        //今天凌晨到结束时间的部分
        hourCount = endTime.substring(11, 13).toInt
        date = endTime.split(" ")(0)
        currTime = date + " 00:00:00"
        for (i <- 0 to hourCount) {
          val hour = currTime.substring(11, 13).toInt //获取当前的小时时间
          var nextTime = ""
          if (hour + 1 < 10) {
            nextTime = currTime.substring(0, 10) + " 0" + (hour + 1) + ":00:00" //个位数补0
          } else {
            nextTime = currTime.substring(0, 10) + " " + (hour + 1) + ":00:00" //得到下一小时的时间
          }
          var dura = 0l
          if (hour == endTime.substring(11, 13).toInt) {
            dura = (date2Timestamp(endTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(currTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
            map += ((date, endTime.substring(11, 13)) -> dura.toString)
          } else {
            dura = (date2Timestamp(nextTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(currTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
            map += ((date, currTime.substring(11, 13)) -> dura.toString)
          }
          currTime = nextTime
        }
        //跨两天
      } else if (startTime < currentDate && endTime >= nextDay) {
        //开始时间到今天凌晨的部分
        var hourCount = 23 - startTime.substring(11, 13).toInt
        var date = currTime.split(" ")(0)
        for (i <- 0 to hourCount) {
          val hour = currTime.substring(11, 13).toInt //获取当前的小时时间
          var nextTime = ""
          if (hour + 1 < 10) {
            nextTime = currTime.substring(0, 10) + " 0" + (hour + 1) + ":00:00" //个位数补0
          } else {
            nextTime = currTime.substring(0, 10) + " " + (hour + 1) + ":00:00" //得到下一小时的时间
          }
          val dura = (date2Timestamp(nextTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(currTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
          map += ((date, currTime.substring(11, 13)) -> dura.toString)
          currTime = nextTime
        }
        //今天凌晨到次日凌晨的部分
        date = currentDate
        currTime = date + " 00:00:00"
        for (i <- 0 to 23) {
          val hour = currTime.substring(11, 13).toInt //获取当前的小时时间
          var nextTime = ""
          if (hour + 1 < 10) {
            nextTime = currTime.substring(0, 10) + " 0" + (hour + 1) + ":00:00" //个位数补0
          } else {
            nextTime = currTime.substring(0, 10) + " " + (hour + 1) + ":00:00" //得到下一小时的时间
          }
          val dura = (date2Timestamp(nextTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(currTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
          map += ((date, currTime.substring(11, 13)) -> dura.toString)
          currTime = nextTime
        }
        //次日凌晨到结束时间
        hourCount = endTime.substring(11, 13).toInt
        date = endTime.split(" ")(0)
        currTime = date + " 00:00:00"
        for (i <- 0 to hourCount) {
          val hour = currTime.substring(11, 13).toInt //获取当前的小时时间
          var nextTime = ""
          if (hour + 1 < 10) {
            nextTime = currTime.substring(0, 10) + " 0" + (hour + 1) + ":00:00" //个位数补0
          } else {
            nextTime = currTime.substring(0, 10) + " " + (hour + 1) + ":00:00" //得到下一小时的时间
          }
          var dura = 0l
          if (hour == endTime.substring(11, 13).toInt) {
            dura = (date2Timestamp(endTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(currTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
            map += ((date, endTime.substring(11, 13)) -> dura.toString)
          } else {
            dura = (date2Timestamp(nextTime, DATE_FORMAT_Hour_Minute_Second) - date2Timestamp(currTime, DATE_FORMAT_Hour_Minute_Second)) / 1000
            map += ((date, currTime.substring(11, 13)) -> dura.toString)
          }
          currTime = nextTime
        }
      }else {
        map += ((currTime.substring(0,10),currTime.substring(11,13)) -> "0")
      }
      map
    }
  }

  /**
    * 直播拆分为分钟
    *
    * @param startTime 开始时间
    * @param endTime   结束时间
    * @return
    */
  def liveTimeByMinute(startTime: String, endTime: String): List[((String,String), (String, String))] = {
    var currTime = startTime
    var etime = endTime
    val startHour = startTime.substring(11, 13) //获取小时
    var endHour = endTime.substring(11, 13)
    var hours = 0
    var mins = 0
    val date = startTime.substring(0,10)
    var list: List[((String,String), (String, String))] = List()

    //day相同，小时相同
    if (startHour == endHour && startTime.substring(8, 10).equals(endTime.substring(8, 10))) {
      var startMinSec = startTime.substring(14, 19) //获取分钟和秒
      var endMinSec = endTime.substring(14, 19)
      if (endMinSec < startMinSec) {
        //println("jiao huan==" + endMinSec + ", " + startMinSec)
        val tmp = endMinSec
        endMinSec = startMinSec
        startMinSec = tmp
      }
      val minList = minuteInHour(startMinSec, endMinSec, 0)
      for (l <- minList) {
        list = list :+((date,startHour), l)
      }
      list
    } else {
      if (etime.substring(8, 10).toInt > currTime.substring(8, 10).toInt) {
        etime = endTime.substring(0, 10) + " 23:59:60" //跨天设置
      }

      endHour = etime.substring(11, 13)
      //println("endHour : " + startHour.toInt + ", " + endHour.toInt)
      for (hour <- startHour.toInt to endHour.toInt) {
        var nextTime = ""
        if (hour + 1 < 10) {
          nextTime = currTime.substring(0, 10) + " 0" + (hour + 1) + ":00:00" //个位数补0
        } else {
          nextTime = currTime.substring(0, 10) + " " + (hour + 1) + ":00:00" //得到下一小时的时间
        }

        if (hour == etime.substring(11, 13).toInt) {
          val startMinSec = currTime.substring(14, 19) //获取分钟和秒
          var endMinSec = etime.substring(14, 19)
          //10:00:00--11:00:00
          if (startMinSec.equals(endMinSec) && endMinSec.equals("00:00")) {
            endMinSec = "59:60"
          }
          val minList = minuteInHour(startMinSec, endMinSec, 1)

          for (l <- minList) {
            list = list :+((date,currTime.substring(11, 13)), l)
          }
        } else {
          val startMinSec = currTime.substring(14, 19) //获取分钟和秒
          var endMinSec = nextTime.substring(14, 19)
          if (startMinSec.equals(endMinSec) && endMinSec.equals("00:00")) {
            endMinSec = "59:60"
          }
          val minList = minuteInHour(startMinSec, endMinSec, 0)

          for (l <- minList) {
            list = list :+((date,currTime.substring(11, 13)), l)
          }
        }

        currTime = nextTime
      }
      list
    }
  }

  /**
    * 计算某一个小时每分钟的时长
    *
    * @param startMinSec 同一小时的开始时间
    * @param endMinSec   同一小时的结束时间
    * @param flag        1 最后一个小时 0：不是最后一个小时
    * @return
    */
  def minuteInHour(startMinSec: String, endMinSec: String, flag: Int): List[(String, String)] = {
    var currMinSec = startMinSec
    val startSec = startMinSec.substring(3, 5) //获取秒
    val endSec = endMinSec.substring(3, 5)
    val startMin = startMinSec.substring(0, 2) //获取分钟
    val endMin = endMinSec.substring(0, 2)
    var list: List[(String, String)] = List()
    var mins = 0
    var cntFlag = 0 //次数标志

    if (endMin.equals("00") && endSec.equals("00")) {
      if (flag == 1) //时间参考:11:00:00--11:00:00
      {
        mins = 0
      } else {
        //时间参考:10:02:11--11:00:00
        mins = 59 - startMin.toInt
      }

    } else {
      mins = endMin.toInt - startMin.toInt
    }

    if (mins == 0) {
      val ms = (endMin, (endSec.toInt - startSec.toInt).toString + ";1")
      list = list :+ ms
      list
    }
    else {
      for (i <- 0 to mins) {
        val min = currMinSec.substring(0, 2).toInt //获取当前的分钟时间
        var nextMin = ""
        if (min + 1 < 10) {
          nextMin = "0" + (min + 1) + ":00" //个位分钟补0
        } else {
          nextMin = (min + 1) + ":00" //得到下一分钟的时间
        }
        var dura = 0

        //不加min != 0当endMinSec为00的话导致第0分钟的时长为0
        if (min == endMinSec.substring(0, 2).toInt && min != 0) {
          //dura = dateTotimestamp(endTime) - dateTotimestamp(currTime)
          dura = endMinSec.substring(3, 5).toInt
          list = list :+(min.toString, dura.toString + ";0")
          cntFlag = 0
        } else {
          dura = 60 - currMinSec.substring(3, 5).toInt
          if (cntFlag == 0) {
            list = list :+(min.toString, dura.toString + ";1")
            cntFlag = 1
          } else {
            list = list :+(min.toString, dura.toString + ";0")
          }
        }

        currMinSec = nextMin
      }
      list
    }
  }

  /**
    * 获取apk打开开始的时间，供后面的时长和次数使用
    *
    * @param duration 时长，毫秒
    * @param endTime  结束时间，格式是2016-11-02 14:59:10
    * @return
    */
  def apkOnTime(duration: String, endTime: String, pattern: String): String = {
    val end = date2Timestamp(endTime, pattern)
    var startTime = timestampToDate((end - duration.toLong).toString, DATE_FORMAT_Hour_Minute_Second)
    val preDate = DateTime.parse(endTime.substring(0, 10)).plusDays(-1).toString("yyyy-MM-dd")
    if (preDate.equals(startTime.substring(0, 10))) {
      startTime = endTime.substring(0, 10) + " 00:00:00"
    }
    startTime
  }

  /**
    * 秒级时间戳转为时间
    *
    * @param timestamp 时间戳  单位：毫秒
    * @return
    */
  def timestampToDate(timestamp: String, pattern: String): String = {
    new SimpleDateFormat(pattern).format(timestamp.toLong)
  }

  /**
    * 毫秒级时间戳转为时间
    *
    * @param timestamp 时间戳  单位：秒
    * @return
    */
  def secondTimestampToDate(timestamp: String, pattern: String): String = {
    val newtimestamp = timestamp + "000"
    new SimpleDateFormat(pattern).format(newtimestamp.toLong)
  }

  /**
    * 时间转为时间戳,单位是毫秒
    *
    * @param date 时间
    * @return
    */
  def date2Timestamp(date: String, pattern: String): Long = {
    new SimpleDateFormat(pattern).parse(date).getTime()
  }

  /**
    * 格式化日期
    *
    * @param date Date对象
    * @return 格式化后的日期
    */
  def formatDate(date: Date, pattern: String): String = {
    new SimpleDateFormat(pattern).format(date)
  }

  /**
    * 获取下一个月的第一天
    *
    * @param dateStr
    * @param pattern
    * @return
    */
  def getFirstDayOfNextMonth(dateStr: String, pattern: String): String = {
    val dft = new SimpleDateFormat(pattern)
    val calendar = java.util.Calendar.getInstance()
    calendar.setTime(dft.parse(dateStr))
    calendar.add(Calendar.MONTH, 1);
    calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
    dft.format(calendar.getTime)
  }

  //时间字符串+天数=>时间戳
  def dateStrAddDays2TimeStamp(dateStr: String, pattern: String, days: Int): Long = {
    datestr2Date(dateStr, pattern).plusDays(days).toDate.getTime
  }

  //时间字符串+天数=>时间字符串
  def dateStrAddDays(dateStr: String, pattern: String, days: Int): String = {
    DateTime.parse(dateStr).plusDays(days).toString(pattern)
  }

  //时间字符串=>日期
  def datestr2Date(dateStr: String, pattern: String): DateTime = {
    new DateTime(new SimpleDateFormat(pattern).parse(dateStr))
  }

  //时间戳=>小时数
  def timeStamp2Hour(timestamp: Long): Long = {
    new DateTime(timestamp).hourOfDay().getAsString().toLong
  }

  //时间戳=>分钟数
  def timeStamp2Minute(timestamp: Long): Long = {
    new DateTime(timestamp).minuteOfHour().getAsString().toLong
  }

  //时间戳=>秒数
  def timeStamp2Second(timestamp: Long): Long = {
    new DateTime(timestamp).secondOfMinute().getAsString.toLong
  }

  //日期=>小时数
  def date2Hour(date: Date): Long = {
    new DateTime(date.getTime).hourOfDay().getAsString().toLong
  }

  //日期=>分钟数
  def date2Minute(date: Date): Long = {
    new DateTime(date.getTime).minuteOfHour().getAsString().toLong
  }

  //日期=>秒数
  def date2Second(date: Date): Long = {
    new DateTime(date.getTime).secondOfMinute().getAsString.toLong
  }

  //日期字符串=>小时数
  def datestr2Hour(date: String): Long = {
    new DateTime(date2Timestamp(date, DATE_FORMAT_Hour_Minute_Second)).hourOfDay().getAsString().toLong
  }

  //日期字符串=>分钟数
  def date2strMinute(date: String): Long = {
    new DateTime(date2Timestamp(date, DATE_FORMAT_Hour_Minute_Second)).minuteOfHour().getAsString().toLong
  }

  //日期字符串=>秒数
  def datestr2Second(date: String): Long = {
    new DateTime(date2Timestamp(date, DATE_FORMAT_Hour_Minute_Second)).secondOfMinute().getAsString.toLong
  }

  //对开始和结束时间分时(一天之内)
  //other_info最后加上"\t"
  def splitTimeByHour(other_info: String, launchTimeStamp: Long, exitTimeStamp: Long): ArrayBuffer[String] = {
    val arr = ArrayBuffer[String]()
    val launchHour = timeStamp2Hour(launchTimeStamp)
    val exitHour = timeStamp2Hour(exitTimeStamp)
    val diffTimeStamp = (exitTimeStamp - launchTimeStamp)
    if (exitHour - launchHour == 0) {
      //其他信息 整点 次数 时长
      arr.append(other_info + launchHour + "\t" + 1 + "\t" + diffTimeStamp / 1000)
    }
    if (exitHour - launchHour > 0) {
      val launchHour = timeStamp2Hour(launchTimeStamp)
      val launchMinute = timeStamp2Minute(launchTimeStamp)
      val launchSec = timeStamp2Second(launchTimeStamp)
      val exitHour = timeStamp2Hour(exitTimeStamp)
      val exitMinute = timeStamp2Minute(exitTimeStamp)
      val exitSec = timeStamp2Second(exitTimeStamp)
      arr.append(other_info + launchHour + "\t" + 1 + "\t" + (60 * 60 - (launchMinute * 60 + launchSec)))
      var h = launchHour
      //需要分时
      while (h < exitHour) {
        h = h + 1
        if (h != exitHour) {
          arr.append(other_info + h +
            "\t" + 0 + "\t" + 1 * 60 * 60)
        } else {
          arr.append(other_info + h + "\t" + 0 + "\t" + (exitMinute * 60 + exitSec))
        }
      }
    }
    arr
  }

  //对开始和结束时间分时
  def splitTimeByMinute(other_info: String, startTime: String, endTime: String): ArrayBuffer[String] = {
    val arr = ArrayBuffer[String]()
    //00:01  00:29
    val startHour = startTime.charAt(0).toString.toInt == 0 match {
      case true => startTime.charAt(1).toString.toInt
      case false => startTime.substring(0, 2).toInt
    }
    val startMin = startTime.charAt(3).toString.toInt == 0 match {
      case true => startTime.charAt(4).toString.toInt
      case false => startTime.substring(3).toInt
    }
    val endHour = endTime.charAt(0).toString.toInt == 0 match {
      case true => endTime.charAt(1).toString.toInt
      case false => endTime.substring(0, 2).toInt
    }
    val endMin = endTime.charAt(3).toString.toInt == 0 match {
      case true => endTime.charAt(4).toString.toInt
      case false => endTime.substring(3).toInt
    }
    val diffHour = endHour - startHour
    if (diffHour == 0) {
      var i = startMin
      while (i <= endMin) {
        arr.append(other_info + startHour.toString + "\t" + i.toString)
        i = i + 1
      }
    }
    if (diffHour > 0) {
      var i = startMin
      while (i <= 59) {
        arr.append(other_info + startHour.toString + "\t" + i.toString)
        i = i + 1
      }
      var h = startHour
      //需要分时
      while (h < endHour) {
        h = h + 1
        if (h != endHour) {
          for (n <- 0 to 59) {
            arr.append(other_info + h.toString + "\t" + n.toString)
          }
        } else {
          for (n <- 0 to endMin) {
            arr.append(other_info + h.toString + "\t" + n.toString)
          }
        }
      }
    }
    arr
  }

  //添加时间格式中的"0"
  def addZero(hourOrMin: String): String = {
    if (hourOrMin.toInt <= 9)
      "0" + hourOrMin
    else
      hourOrMin
  }

  //去除时间格式中的"0"
  def delZero(hourOrMin: String): String = {
    var res = hourOrMin
    if (!hourOrMin.equals("0") && hourOrMin.startsWith("0"))
      res = res.replaceAll("^0", "")
    res
  }

  //获取昨天的日期
  def getYesterday(currentDate:String) = {
    DateTime.parse(currentDate).plusDays(-1).toString("yyyy-MM-dd")
  }

  //获取7天前的日期
  def getSevenDay(currentDate:String) = {
    DateTime.parse(currentDate).plusDays(-6).toString("yyyy-MM-dd")
  }

  //获取30天前的日期
  def getMonthAgo(currentDate:String) = {
    DateTime.parse(currentDate).plusDays(-29).toString("yyyy-MM-dd")
  }

  /**
    * 获取某天对应的周月
    *
    * @param sc   sparkcontext
    * @param sql  sql
    * @param flag 是否包含*
    * @return
    */
  def getWeek(sc: SparkContext, sql: String, flag: Boolean): (String, String, String, String) = {
    val hiveContext = new HiveContext(sc)

    println(sql)
    var dm = "" //dayformonth
    var w = "" //week
    var dw = "" //dayforweek
    var mon = "" //month

    if (flag == true) {
      return (w, dw, mon, dm)
    }
    val hiveDataFrame = hiveContext.sql(sql)

    //hiveDataFrame.rdd.collect().foreach(println)

    w = hiveDataFrame.first()(2).toString
    dw = hiveDataFrame.first()(3).toString
    mon = hiveDataFrame.first()(4).toString
    dm = hiveDataFrame.first()(1).toString

    (w, dw, mon, dm)
  }

  /**
    * 获取每周的第一天
    *
    * @param sc  sparkcontext
    * @param sql sql
    * @return
    */
  def getFirstOfWeek(sc: SparkContext, sql: String): String = {
    val hiveContext = new HiveContext(sc)

    println(sql)
    val hiveDataFrame = hiveContext.sql(sql)
    val firstOfWeek = hiveDataFrame.first()(0).toString() // first day of week

    firstOfWeek
  }

  /**
    * 得到当前日期所在月的最后一天的日期
    *
    * @param date
    * @return
    */
  def getWeekEndDate(date: String): String = {
    val week = date.split("-")(1)
    val num_31: String = date.substring(0, 8) + "31"
    val num_30: String = date.substring(0, 8) + "30"
    val num_28: String = date.substring(0, 8) + "28"
    val weekMap = Map(
      ("01" -> num_31),
      ("02" -> num_28),
      ("03" -> num_31),
      ("04" -> num_30),
      ("05" -> num_31),
      ("06" -> num_30),
      ("07" -> num_31),
      ("08" -> num_31),
      ("09" -> num_30),
      ("10" -> num_31),
      ("11" -> num_30),
      ("12" -> num_31)
    )
    weekMap.get(week).getOrElse("2018-10-01")
  }

  //得到当前日期n天前的日期
  def getNDayAgo(i:Int):String = {
    val dateFormat: SimpleDateFormat = new SimpleDateFormat(DATE_FORMAT)
    val now = dateFormat.format(new Date())
    DateTime.parse(now).plusDays(i).toString(DATE_FORMAT)
  }

  //获取指定日期n天前的日期
  def getNDayAgo2(i:Int,date:String) = {
    DateTime.parse(date).plusDays(i).toString(DATE_FORMAT)
  }

  //获取当天日期
  def getNowDate(pattern: String) = {
    new SimpleDateFormat(pattern).format(new Date())
  }

  //获取当前年
  def getYear() = {
    new SimpleDateFormat("yyyy").format(new Date())
  }

  //获取当前月
  def getMonth() = {
    new SimpleDateFormat("MM").format(new Date())
  }

  //获取当前年、月
  def getYearMonth() = {
    new SimpleDateFormat("yyyy-MM").format(new Date())
  }

  /**
    * 日期标准化
    * 增补日期中缺失的"0"
    * @param date
    * @param separatorChar
    * @return
    */
  def dateStandard(date:String,separatorChar:String) = {
    val fields = date.split(separatorChar)
    val year = fields(0)
    val month = if (fields(1).length==2) fields(1) else "0" + fields(1)
    val day = if (fields(2).length==2) fields(2) else "0" + fields(2)
    val standardDate = year + separatorChar + month + separatorChar + day
    standardDate
  }

  def main(args: Array[String]) {
    /*val date: String = getNowDate(DATE_FORMAT_yyyyMMdd)
    println(date)*/
    /*val year = new SimpleDateFormat("MM").format(new Date())
    println(year)*/
    println(getYearMonth())
    val zero: String = DateUtils.delZero("20")
    println(zero)
  }
}