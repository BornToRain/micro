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
    case e: Created => state = Some(Demo(e.id, e.name))
    case e: Updated => state = state map (_ copy (name = e.name))
    case _: Deleted => state = None
  }
  override def receiveCommand                      =
  {
    case c: Create => persist(Created(c.id, c.name))(afterPersist)
    case c: Update => persist(Updated(c.id, c.name))(afterPersist)
    case c: Delete => persist(Deleted(c.id))(afterPersist)
  }
  override def persistenceId                       = self.path.name
}

object DemoAggregate
{
  final val NAME = "demo-aggregate"

  def props = Props(new DemoAggregate)
}
