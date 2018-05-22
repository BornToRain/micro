package org.ryze.micro.demo

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import akka.contrib.persistence.mongodb.MongoReadJournal
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.scaladsl.EventsByTagQuery
import com.typesafe.config.ConfigFactory
import org.ryze.micro.core.actor.{ActorFactory, ActorSupervisor}
import org.ryze.micro.demo.application.service.DemoService
import org.ryze.micro.demo.domain.{DemoAggregate, DemoReadSide}
import org.ryze.micro.demo.infrastructure.{DemoRepositoryImpl, MongoDBClient}
import org.ryze.micro.demo.interfaces.api.v1.DemoApi

object DemoStartUp extends App
{
  val config = ConfigFactory.load("dev.conf")

  implicit val factory = ActorFactory(config)

  factory.create(DemoApp.props)(DemoApp.NAME)
}

class DemoApp(implicit factory: ActorFactory) extends Actor with ActorLogging with ActorSupervisor
{
  import factory.runtime._

  private[this] val readJournal = PersistenceQuery(factory.system).readJournalFor[EventsByTagQuery](MongoReadJournal.Identifier)
  private[this] val mongodb     = MongoDBClient(factory.config)
  private[this] val repository  = DemoRepositoryImpl(mongodb.db)
  private[this] val domain      = factory.cluster.shard(DemoAggregate.NAME)(DemoAggregate.props)
//    create(DemoAggregate.props)(DemoAggregate.NAME)
  private[this] val read        = factory.cluster.shard(DemoReadSide.NAME)(DemoReadSide.props(readJournal, repository))
//    create(DemoReadSide.props(readJournal, repository))(DemoReadSide.NAME)
  private[this] val service     = create(DemoService.props(domain, read))(DemoService.NAME)
  private[this] val api         = create(DemoApi.props(service))(DemoApi.NAME)

  context watch domain
  context watch read
  context watch service
  context watch api

  override def receive =
  {
    case Terminated(ref) => log.info(s"Actor已经关闭: ${ref.path}")
      context.system.terminate
  }
}

object DemoApp
{
  final val NAME = "demo-app"

  def props(implicit factory: ActorFactory) = Props(new DemoApp)
}