package cn.common.tools.common

/**
  * Created by wpq on 2018/9/20.
  * 包名工具类
  */
object PackageConvert {

  val packageMap = Map(
    ("银河·奇异果" -> "爱奇艺"),
    ("腾讯视频TV端" -> "腾讯"),
    ("CIBN环球影视" -> "优酷"),
    ("银河奇异果" -> "爱奇艺"),
    ("云视听极光" -> "腾讯"),
    ("CIBN酷喵影视" -> "优酷"),
    ("tencent" -> "腾讯"),
    ("iqiyi" -> "爱奇艺")
  )

  /**
    * 根据包名转换为对用的媒体
    *
    * @param packageName
    * @return
    */
  def package2media(packageName: String): String = {
    packageMap.get(packageName).getOrElse("其他")
  }

  def main(args: Array[String]) {
    val package2media1: String = PackageConvert.package2media("银河·奇异果")
    println(package2media1)
  }
}
