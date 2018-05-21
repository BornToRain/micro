package org.ryze.micro.demo.domain

import akka.actor.{Actor, ActorLogging, Props}
import akka.persistence.query.scaladsl.EventsByTagQuery
import akka.stream.Materializer
import akka.stream.scaladsl.Sink
import org.ryze.micro.demo.domain.event.{Created, Deleted, Updated}
import akka.persistence.query.Offset

/**
  * Demo读边处理器
  */
class DemoReadSideProcessor(readJournal: EventsByTagQuery, repository: DemoRepository)
(implicit materializer: Materializer) extends Actor with ActorLogging
{
  import akka.contrib.persistence.mongodb
//  readJournal.eventsByPersistenceId(DemoAggregate.NAME, 0L, Long.MaxValue) map
//  {
//    e => log.info(s"event: ${e.event}")
//      log.info(s"offset: ${e.offset}")
//      e.event
//  } runWith Sink.actorRef(self, "completed")

  readJournal.eventsByTag("demo-tag", Offset.noOffset) map
  {
    e => log.info(s"event: ${e.event}")
      log.info(s"offset: ${e.offset}")
      e.event
  } runWith Sink.actorRef(self, "completed")

  override def receive =
  {
    case e: Created  => repository.insert(Demo(e.id, e.name))
    case e: Updated  => repository.update(Demo(e.id, e.name))
    case Deleted(id) => repository.delete(id)
  }
}

object DemoReadSideProcessor
{
  final val NAME = "demo-read"

  def props(readJournal: EventsByTagQuery, repository: DemoRepository)(implicit materializer: Materializer) =
    Props(new DemoReadSideProcessor(readJournal, repository))
}