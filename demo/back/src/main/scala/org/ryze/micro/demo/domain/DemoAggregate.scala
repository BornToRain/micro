package org.ryze.micro.demo.domain

import akka.actor.Props
import org.ryze.micro.core.domain.AggregateRoot
import org.ryze.micro.demo.domain.event.{Created, Deleted, Updated}
import org.ryze.micro.demo.protocol._

class DemoAggregate extends AggregateRoot[Option[Demo], DemoCommand, DemoEvent]
{
  override var state: Option[Demo] = _

  override def updateState(event: DemoEvent): Unit = event match
  {
    case e: Updated => state = state map (_ copy (name = e.name))
    case e: Created => state = Some(Demo(e.id, e.name))
    case _: Deleted => state = None
  }
  override def receiveCommand                      =
  {
    case c: Update => persist(c.event)(afterPersist)
    case c: Create => persist(c.event)(afterPersist)
    case c: Delete => persist(c.event)(afterPersist)
  }
  override def persistenceId                       = DemoAggregate.NAME
}

object DemoAggregate
{
  final val NAME = "demo-aggregate"

  def props = Props(new DemoAggregate)
}
