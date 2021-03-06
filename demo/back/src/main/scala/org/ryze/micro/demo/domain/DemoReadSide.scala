package org.ryze.micro.demo.domain

import akka.actor.{Actor, ActorLogging, Props}
import akka.pattern._
import akka.persistence.query.Offset
import akka.persistence.query.scaladsl.EventsByTagQuery
import akka.stream.scaladsl.Sink
import org.ryze.micro.core.actor.ActorRuntime
import org.ryze.micro.demo.domain.event.{Created, Deleted, Updated}
import org.ryze.micro.demo.protocol.{DemoEvent, GetState}

/**
  * Demo读边处理器
  */
class DemoReadSide(readJournal: EventsByTagQuery, repository: DemoRepository)
(implicit runtime: ActorRuntime) extends Actor with ActorLogging
{
  import runtime._

  //根据事件标签获取实时事件流
  readJournal.eventsByTag(DemoEvent.TAG, Offset.noOffset) map (_.event) runWith Sink.actorRef(self, "completed")

  override def receive =
  {
    case GetState(id) => repository.selectOne(id) pipeTo sender
    case e: Updated   => repository.update(Demo(e.id, e.name))
    case e: Created   => repository.insert(Demo(e.id, e.name))
    case Deleted(id)  => repository.delete(id)
  }
}

object DemoReadSide
{
  final val NAME = "demo-read"

  def props(readJournal: EventsByTagQuery, repository: DemoRepository)(implicit runtime: ActorRuntime) =
    Props(new DemoReadSide(readJournal, repository))
}