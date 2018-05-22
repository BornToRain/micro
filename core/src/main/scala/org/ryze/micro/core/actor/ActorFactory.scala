package org.ryze.micro.core.actor

import akka.actor.{ActorSystem, Props}
import com.typesafe.config.Config
import org.ryze.micro.core.cluster.DefaultClusterFactory


/**
  * Akka工厂
  */
case class ActorFactory(config: Config)
{
  implicit val system  = ActorSystem(config.getString("akka.cluster.name"), config)
  //Actor运行环境隐式量
  implicit val runtime = ActorRuntime()
  lazy val cluster = new DefaultClusterFactory(config.getInt("akka.cluster.max-nodes") * 10)

  //创建Actor
  @inline
  def create(props: Props)(name: String) = system actorOf(props, name)
}
