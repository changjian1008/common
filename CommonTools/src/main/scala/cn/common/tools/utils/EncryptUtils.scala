package cn.common.tools.utils

import java.net.URLDecoder
import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object EncryptUtils {
  def toMd5One(s: String) = {
    val m = java.security.MessageDigest.getInstance("MD5")
    val b = s.getBytes("UTF-8")
    m.update(b, 0, b.length)
    new java.math.BigInteger(1, m.digest()).toString(16)
  }

  def toMd5Two(text: String): String = {
    val digest = MessageDigest.getInstance("MD5")
    digest.digest(text.getBytes).map("%02x".format(_)).mkString

  }

  def md5Hash(text: String): String =
    java.security.MessageDigest.getInstance("MD5").digest(text.getBytes()).map(0xFF & _).map {
      "%02x".format(_)
    }.foldLeft("") {
      _ + _
    }

  /**
    * 中文转码
    * @param value "%2B%10C%15A"
    * @return
    */
  def urlDecode(value: String): String = {
    URLDecoder.decode(value, "UTF8")
  }

  /**
    * 中文转码
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
        i += 1; i - 1
      }
    }
    new String(bytes)
  }

  def mac2Avcid(mac:String,deviceType:String): String = {
    val avcid = EncryptUtils.toMd5Two(deviceType + mac.replace(":","").toLowerCase + "91110111MA01D64D71")
    avcid
  }

  def byteArray2Str(byteArray:Array[Byte]) = {
    new String(byteArray, StandardCharsets.UTF_8)
  }

  def main(args: Array[String]) {
    //    val text = "hahaha"
    //    println(toMd5One(text))
    //    println(toMd5Two(text))
    //    println(md5Hash(text))
    println(mac2Avcid("aa:bb:cc:dd:ee:ff","01"))
  }
}
