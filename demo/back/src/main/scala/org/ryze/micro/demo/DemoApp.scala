package org.ryze.micro.demo

import akka.actor.{Actor, ActorContext, ActorLogging, Props, Terminated}
import akka.contrib.persistence.mongodb.MongoReadJournal
import akka.persistence.query.PersistenceQuery
import akka.persistence.query.scaladsl.EventsByTagQuery
import com.typesafe.config.{ConfigFactory, ConfigValueFactory}
import org.ryze.micro.core.actor.{ActorFactory, ActorSupervisor}
import org.ryze.micro.demo.application.service.DemoService
import org.ryze.micro.demo.domain.{DemoAggregate, DemoReadSideProcessor}
import org.ryze.micro.demo.infrastructure.{DemoRepositoryImpl, MongoDBClient}
import org.ryze.micro.demo.interfaces.api.v1.DemoApi
import org.ryze.micro.demo.DemoStartUp.factory

import scala.concurrent.Future

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
  private[this] val mongodb    = MongoDBClient(factory.config)
  private[this] val repository = DemoRepositoryImpl(mongodb.db)
  private[this] val domain     = context actorOf(DemoAggregate.props, DemoAggregate.NAME)
  private[this] val read       = context actorOf(DemoReadSideProcessor.props(readJournal, repository), DemoReadSideProcessor.NAME)
  private[this] val service    = context actorOf(DemoService.props(domain, repository), DemoService.NAME)
  private[this] val api        = context actorOf(DemoApi.props(service), DemoApi.NAME)

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