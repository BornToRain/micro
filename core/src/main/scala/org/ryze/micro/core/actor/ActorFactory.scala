package org.ryze.micro.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.typesafe.config.Config


/**
  * Akka工厂
  */
case class ActorFactory(config: Config)
{
  implicit val system  = ActorSystem(config.getString("akka.cluster.name"), config)
  //Actor运行环境隐式量
  implicit val runtime = ActorRuntime()

  //创建Actor
  def create(props: Props)(name: String) = system actorOf(props, name)
}
