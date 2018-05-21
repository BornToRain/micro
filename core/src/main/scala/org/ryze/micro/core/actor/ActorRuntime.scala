package org.ryze.micro.core.actor

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/**
  * Actor运行环境
  */
case class ActorRuntime(implicit system: ActorSystem)
{
  implicit val ec: ExecutionContext = system.dispatcher
  implicit val materializer         = ActorMaterializer()
  implicit val timeout              = Timeout(5.seconds)
}
