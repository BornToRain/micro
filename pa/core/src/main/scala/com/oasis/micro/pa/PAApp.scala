package com.oasis.micro.pa

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import com.oasis.micro.pa.infrastructure.ConfigLoader
import com.oasis.micro.pa.infrastructure.service.{OasisClient, PAClient, RdsClient}
import com.oasis.micro.pa.interfaces.api.v1.PAApi
import org.ryze.micro.core.actor.{ActorFactory, ActorSupervisor}

object PAStartUp extends App with ConfigLoader
{
  implicit val factory = ActorFactory(loader)

  factory create(PAApp.props)(PAApp.NAME)
}

/**
  * 平安京管家模块
  */
class PAApp(implicit factory: ActorFactory) extends Actor with ActorLogging with ActorSupervisor
{
  import factory.runtime

  private[this] val redis = RdsClient(factory.config).rds
  private[this] val client = PAClient(redis)
  private[this] val oasis  = OasisClient()
  private[this] val api   = create(PAApi.props(client, oasis))(PAApi.NAME)

  context watch api

  override def receive =
  {
    case Terminated(ref) => log.info(s"Actor已经关闭: ${ref.path}")
      context.system.terminate
  }
}

object PAApp
{
  final val NAME = "pa-app"

  def props(implicit factory: ActorFactory) = Props(new PAApp())
}