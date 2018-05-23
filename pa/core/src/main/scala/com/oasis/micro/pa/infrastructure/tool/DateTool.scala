package com.oasis.micro.pa.infrastructure.tool

import java.util.{Date, Locale}

import org.joda.time.{DateTime, DateTimeZone, Duration}
import org.joda.time.format.DateTimeFormat

object DateTool
{
  lazy val SECONDS    = "seconds"
  lazy val MINUTES    = "minutes"
  lazy val HOURS      = "hours"
  lazy val DAYS       = "days"
  lazy val FULLDATE   = "yyyy-MM-dd HH:mm:ss"
  lazy val TIMESTAMP  = "yyyyMMddHHmmss"
  lazy val RFC822DATE = "EEE, dd MMM yyyy HH:mm:ss z"

  /**
    * yyyyMMddHHmmss格式时间戳
    */
  def datetimeStamp = DateTime.now.toString(TIMESTAMP)
  /**
    * 解析日期字符串
    */
  def parse(s: String)(implicit pattern: String = FULLDATE) = DateTime.parse(s, DateTimeFormat.forPattern(pattern)
  .withLocale(Locale.US).withZone(DateTimeZone.getDefault)).toDate
  /**
    * 日期格式化
    */
  def format(date: Date)(implicit pattern: String = FULLDATE) = DateTimeFormat.forPattern(pattern).withLocale(Locale.US)
  .withZone(DateTimeZone.getDefault).print(date.getTime)
  /**
    * x与y的时间差
    * x<y?正数:复数 默认单位分钟
    */
  def compareDate(x: Date)(y: Date)(`type`: String = MINUTES) =
  {
    val d = new Duration(new DateTime(x), new DateTime(y))
    `type` match
    {
      case SECONDS => d.getStandardSeconds
      case MINUTES => d.getStandardMinutes
      case HOURS   => d.getStandardHours
      case DAYS    => d.getStandardDays
      case _       => d.getMillis
    }
  }
}
