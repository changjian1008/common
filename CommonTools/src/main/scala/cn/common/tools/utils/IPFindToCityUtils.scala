package cn.common.tools.utils

import org.apache.commons.lang3.StringUtils
import org.apache.spark.broadcast.Broadcast

object IPFindToCityUtils {
  /**
   * 二分查找
   *
   * @param arr
   * @param ip
   * @return
   */
  def binarySearch(arr: Array[(Long, Long, String, String, String, String, String)], ip: Long): Int = {

    var l = 0
    var h = arr.length - 1
    while (l <= h) {
      var m = (l + h) / 2
      if ((ip >= arr(m)._1) && (ip <= arr(m)._2)) {
        return m
      } else if (ip < arr(m)._1) {
        h = m - 1
      } else {
        l = m + 1
      }
    }
    -1
  }
  /**
   * 采用二分查找法查找当前IP对应的地市信息
   */
  def changeIp(bd: Broadcast[Array[(Long, Long, String, String, String, String, String)]], ipString: String): (Long, Long, String, String, String, String, String) = {
    val ip = IPUtil.ipToLong(ipString)
    val arr = bd.value
    val i = this.binarySearch(arr, ip)
    if (i >= 0) {
      arr(i)
    } else {
      (-1, -1, "", "", "", "","")
    }
  }
  /**
   * 采用二分查找法查找当前IP对应的地市信息
   */
  def ipToRegion(arr: Array[(Long, Long, String, String, String, String, String)], ipString: String): (Long, Long, String, String, String, String, String) = {
    val ip: Long = IPUtil.ipToLong(ipString)
    val i = this.binarySearch(arr, ip)

    if (i >= 0) {
      val d = arr(i)
      d
    } else {
      val d = (-1l, -1l, "", "", "", "", "")
      d
    }
  }
  def provinceid(areaid: String): String = {

    //第一、二位表示省
    if (StringUtils.isNotBlank(areaid) && areaid.length() > 2) {
      areaid.substring(0, 2)
    } else {
      "-1"
    }
  }
  def cityid(areaid: String): String = {
    //第三、四位表示市
    if (StringUtils.isNotBlank(areaid) && areaid.length() > 4) {
      areaid.substring(0, 4)
    } else {
      "-1"
    }
  }
}