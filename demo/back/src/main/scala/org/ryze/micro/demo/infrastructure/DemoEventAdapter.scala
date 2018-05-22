package org.ryze.micro.demo.infrastructure

import akka.persistence.journal.{Tagged, WriteEventAdapter}
import org.ryze.micro.demo.protocol.DemoEvent

/**
  * Demo事件适配
  */
class DemoEventAdapter extends WriteEventAdapter
{
  override def manifest(event: Any)  = ""
  override def toJournal(event: Any) = event match
  {
    case e: DemoEvent => Tagged(e, Set(DemoEvent.TAG))
  }
}
