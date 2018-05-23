package com.oasis.micro.pa

import com.oasis.micro.pa.domain.event.Paid
import com.oasis.micro.pa.domain.event.{OrderDetail => OD}
import org.ryze.micro.protocol.domain.{DomainCommand, DomainEvent}

import scala.language.postfixOps

/**
  * 平安金管家领域协议
  */
package object protocol
{
  /**
    * 领域命令
    */
  sealed trait PACommand extends DomainCommand
  {
    val id   : String
    val event: PAEvent
  }
  case class Pay(id: String, orderId: String, openId: String, amount: Int, payType: String, details: Seq[OrderDetail],
    detailUri: String) extends PACommand
  {
    val xs = details map (d => OD(d.id, d.name, d.count, d.uri orNull, d.imageUri orNull, d.subject orNull, d.amount))
    override val event = Paid(id, orderId, openId, amount, payType, xs, detailUri)
  }

  /**
    * 领域事件
    */
  trait PAEvent extends DomainEvent

  /**
    * 值对象
    */
  trait PAValueObject
  case class OrderDetail(id: String, name: String, count: Int, uri: Option[String] = Some("null"), imageUri: Option[String] = Some("null"),
    subject: Option[String] = Some("null"), amount: Int) extends PAValueObject
  case class User(openId: String, mobilePhone: Option[String], agent: Option[String], empType: Option[String], alias: Option[String],
    userType: Option[String])                            extends PAValueObject
}
