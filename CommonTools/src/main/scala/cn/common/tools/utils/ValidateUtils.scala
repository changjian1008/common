package cn.common.tools.utils

import java.util.regex.Pattern


/**
  * @author wpq
  * @define 验证工具类
  */
object ValidateUtils {
  /**
    * 判断是否是数字
    * @param s
    * @return
    */
  def isNumber(s: String) = {
    val pattern = """^(\d+)$""".r
    s match {
      case pattern(_*) => true
      case _ => false
    }
  }

  /**
    * 格式化小数
    * @param num Double对象
    * @param scale 四舍五入的位数
    * @return 格式化后的小数
    */
  def formatDouble(num: Double, scale: Int)={

    val decimal = BigDecimal.apply(num)
    decimal.setScale(scale,BigDecimal.RoundingMode.HALF_UP).doubleValue()

  }

  /**
    * 是否包含乱码
    * @param str
    * @return
    */
  def isContainsMessyCode(str: String): Boolean = {

    //    汉字：[0x4e00,0x9fa5]（或十进制[19968,40869]）
    //    数字：[0x30,0x39]（或十进制[48, 57]）
    //    小写字母：[0x61,0x7a]（或十进制[97, 122]）
    //    大写字母：[0x41,0x5a]（或十进制[65, 90]）
    val res = str.replaceAll("[\u4e00-\u9fa5]", "").replaceAll("\\d|\\w", "")
    println("res:" + res)
    !res.isEmpty
  }


  /**
    * 判断是否包含中文
    * @param str
    * @return
    */
  def isContainsCN(str: String): Boolean = {
    val p = Pattern.compile("[\u4e00-\u9fa5]")
    val m = p.matcher(str)

    m.find()
  }

  /**
    * 判断是否包含字母
    * @param str
    * @return
    */
  def isContainsLetter(str: String): Boolean = {
    val p = Pattern.compile("[a-zA-Z]")
    val m = p.matcher(str)

    m.find()
  }



  /**
    * 判断是否包含特殊字符
    * @param str
    * @return
    */
  def isContainsSpeciChar(str: String): Boolean = {
    val regEx = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
    val p = Pattern.compile(regEx);
    val m = p.matcher(str);

    m.find()
  }

  /**
    * 判断是否包含指定的关键词
    * @param str
    * @param keywordArr
    * @return
    */
  def isContainsSpecWords(str: String, keywordArr: Array[String]): Boolean = {
    var result = false
    for (ele <- keywordArr if !result) {
      result = str.contains(ele)
    }
    result
  }


  def regxpTest(str: String): Boolean = {
    val regex = """(\d{8})(.+)[[ ]*|_*|\d|-第*集|第\d集|(第\d集)|大结局|先导集]{1}""".r
    println(!regex.findFirstMatchIn(str).isEmpty)
    !regex.findAllIn(str).isEmpty
  }

  def getLoggerInfo(str: String, key: String, ignore: Boolean): String = {
    var igStr = ""
    if (ignore) {
      igStr = "(?i)"
    }
    val regex = igStr + "[\\s\\S]*[<\\[]\\s*" + key + "\\s*[>\\:\\]]\\s*(\\-\\s*\\[)?\\s*([^\\[\\]<]*)[\\s<\\]]+[\\s\\S]*"
    println("regex:" + regex)
    str.replaceAll(regex, "$2")

  }

  //sn校验，只包含字母,数字，下划线
  def snIsRight(s : String) = {
    val pattern = """^([0-9a-zA-Z][0-9a-zA-Z:_]+)$""".r
    s match {
      case pattern(_*) => true
      case _ => false
    }
  }

  //sn校验，过滤包含小写字母的sn
  def snContainsLower(s:String) = {
    val pattern = """^([0-9a-zA-Z]*[a-z]+[0-9a-zA-Z:_]*)$""".r
    s match {
      case pattern(_*) => true
      case _ => false
    }
  }

}
