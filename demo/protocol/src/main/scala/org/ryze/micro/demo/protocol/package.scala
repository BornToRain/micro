package org.ryze.micro.demo

import org.ryze.micro.protocol.domain.{DomainCommand, DomainEvent}

package object protocol
{
  sealed trait DemoCommand extends DomainCommand
  case class Create(id: String, name: String) extends DemoCommand
  case class Update(id: String, name: String) extends DemoCommand
  case class Delete(id: String)               extends DemoCommand
  case class Get(id: String)                  extends DemoCommand

  trait DemoEvent extends DomainEvent
}
