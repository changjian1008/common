package cn.common.tools.common

/**
  * Created by wpq on 2019/3/15.
  * 异常匹配工具类
  */
object ExceptionMate {
  val exceptions = List(
    ("OutOfMemory", "内存溢出"),
    ("is running beyond physical memory limit", "集群资源超分配"),
    ("java.lang.OutOfMemory, unable to create new native thread", "无法创建更多的线程数"),
    ("org.apache.spark.sql.AnalysisException", "无法正常解析的语句"),
    ("InvalidInputException: Input path does not exist","文件路径不存在"),
    ("java.io.IOException: No FileSystem for scheme: http","没有这样的文件系统"),
    ("RuntimeException","运行时异常"),
    ("NullPointerException","空指针异常"),
    ("ClassCastException","类型转换异常"),
    ("IndexOutOfBoundsException","角标越界异常"),
    ("UnsupportedOperationException","不支持的操作类型"),
    ("NoSuchElementException","没有这样的元素"),
    ("NumberFormatException","数字转换异常"),
    ("InterruptedException ","线程终端异常"),
    ("Kryo serialization failed: Buffer overflow","超出序列化缓冲区大小，尝试设置更大的缓冲区"),
    ("ERROR netty.Inbox: Ignoring error","netty通信异常"),
    ("BatchUpdateException","mysql批处理异常"),
    ("SparkContext was shut down","任务异常结束")
  )
}
