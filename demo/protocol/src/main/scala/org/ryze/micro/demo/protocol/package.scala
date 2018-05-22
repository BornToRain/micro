package org.ryze.micro.demo

import org.ryze.micro.demo.domain.event.{Created, Deleted, Updated}
import org.ryze.micro.protocol.domain.{DomainCommand, DomainEvent}

/**
  * Demo领域协议
  */
package object protocol
{
  /**
    * Demo领域命令
    */
  sealed trait DemoCommand extends DomainCommand
  {
    val id   : String
    val event: DemoEvent
  }
  case class Create(id: String, name: String) extends DemoCommand
  {
    override val event = Created(id, name)
  }
  case class Update(id: String, name: String) extends DemoCommand
  {
    override val event = Updated(id, name)
  }
  case class Delete(id: String)               extends DemoCommand
  {
    override val event = Deleted(id)
  }

  /**
    * Demo领域事件 protobuf序列化
    */
  trait DemoEvent extends DomainEvent
  object DemoEvent
  {
    //事件标签
    final val TAG = "demo-tag"
  }

  /**
    * Demo状态
    */
  trait DemoState
  case class GetState(id: String) extends DemoState
}
