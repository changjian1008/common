package cn.common.tools.utils

import org.apache.spark.rdd.RDD

/**
  * Created by wpq on 2018/11/27.
  * 获取中位数
  */
object MedianUtils {

  /**
    * 获取rdd数据中的中位数
    * @param rdd
    * @return
    */
  def getMedian(rdd: RDD[(String, Long)]): Long = {
    val median: Long = rdd.mapPartitions(items => {
      items.map(item => {
        ("a", List(item._2))
      })
    }).reduceByKey((left, right) => left ::: right).mapPartitions(items => {
      items.map(item => {
        val list = item._2.sorted
        val half = list.length / 2

        (list(half) + list(list.length + (~half))) / 2
      })
    }).first()

    median
  }
}
