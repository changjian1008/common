package cn.common.tools.utils

import org.apache.commons.lang3.StringUtils
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

object IpaddrFromHDFS {

  def init(sc: SparkContext, ipLibraryPath: String): RDD[(Long, Long, String, String, String, String, String)] = {
      val fileRdd = sc.textFile(ipLibraryPath)
      val allRdd = fileRdd.mapPartitions(f => {
        f.map(temp =>
          {
            //起始IP段\t结束IP段\t国家\t省\t城市\t组织或单位名称\t运营商\t经度\t维度\tTimeZone1\tTimeZone2\t行政区划编码\t国家号码\t国家简称\t洲简称
            StringUtils.splitPreserveAllTokens(temp.replaceAll("""\*""", ""), '\t')
          }).filter(f => f.length >= 15).map(token => {
          val bgn: String = token.apply(0)
          val end: String = token.apply(1)
          val bgnNum:Long = IPUtil.ipToLong(bgn)
          val endNum:Long= IPUtil.ipToLong(end)
          val country: String = token.apply(2)
          val province: String = token.apply(3)
          val city: String = token.apply(4)
          val org: String = token.apply(5)
          val isp: String = token.apply(6)
          val lng: String = token.apply(7)
          val lat: String = token.apply(8)
          val tz1: String = token.apply(9)
          val tz2: String = token.apply(10)
          val areaid: String = token.apply(11)
          val countryid: String = token.apply(12)
          val nation: String = token.apply(13)
          val continent: String = token.apply(14)

          (bgnNum, endNum, country, province, city, areaid,countryid)
        })

      })

      allRdd.distinct().sortBy(_._1)
    }
}

