package cn.common.tools.utils

import java.net.URLDecoder
import java.nio.charset.StandardCharsets

/**
  * Created by wpq on 2019/2/19.
  */
object DecodeUtils {
  /**
    * 中文转码
    *
    * @param value "%2B%10C%15A"
    * @return
    */
  def urlDecode(value: String): String = {
    URLDecoder.decode(value, "UTF8")
  }

  /**
    * 中文转码
    *
    * @param hexStr "\xE8\xBF\xAA\xE4\xBC\x98\xE7\xBE\x8E\xE7\x89\xB9"
    * @return
    */
  def hexStr2Str1(hexStr: String): String = {
    val str = "0123456789ABCDEF"
    val hexs = hexStr.toCharArray
    val bytes = new Array[Byte](hexStr.length / 2)
    var n = 0
    var i = 0
    while ( {
      i < bytes.length
    }) {
      n = str.indexOf(hexs(2 * i)) * 16
      n += str.indexOf(hexs(2 * i + 1))
      bytes(i) = (n & 0xff).toByte

      {
        i += 1;
        i - 1
      }
    }
    new String(bytes)
  }

  def byteArray2Str(byteArray:Array[Byte]) = {
    new String(byteArray, StandardCharsets.UTF_8)
  }
}
