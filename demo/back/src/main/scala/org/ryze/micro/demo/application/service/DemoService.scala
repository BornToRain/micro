package org.ryze.micro.demo.application.service

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.pattern._
import akka.util.Timeout
import cats.data.OptionT
import org.ryze.micro.core.tool.IdWorker
import org.ryze.micro.demo.application.service.DemoService.Request
import org.ryze.micro.demo.domain.{Demo, DemoRepository}
import org.ryze.micro.demo.interfaces.dto.DemoDTO
import org.ryze.micro.demo.protocol.{Create, Delete, Update}

import scala.concurrent.ExecutionContext
import scala.language.postfixOps
import cats.implicits._

class DemoService(domain: ActorRef, repository: DemoRepository)(implicit timeout: Timeout, ec: ExecutionContext) extends Actor with ActorLogging
{
  private[this] def create(r: Request.Create) =
  {
    val id = IdWorker.getId
    domain ! Create(id, r.name)
    id
  }
  private[this] def update(r: Update) = OptionT((domain ? r).mapTo[Option[Demo]]) map (d => DemoDTO(d.name)) value
  private[this] def delete(r: Delete) = OptionT((domain ? r).mapTo[Option[Demo]]) map (_ => true) getOrElse false
  private[this] def get(id: String)   = OptionT(repository.selectOne(id)) map (d => DemoDTO(d.name)) value

  override def receive =
  {
    case r: Request.Create => sender ! create(r)
    case r: Update         => update(r) pipeTo sender
    case r: Delete         => delete(r) pipeTo sender
    case Request.Get(id)   => get(id) pipeTo sender
  }
}

object DemoService
{
  final val NAME = "demo-service"

  def props(domain: ActorRef, repository: DemoRepository)(implicit timeout: Timeout, ec: ExecutionContext) =
    Props(new DemoService(domain, repository))

  sealed trait Request
  object Request
  {
    case class Create(name: String) extends Request
    case class Get(id: String)      extends Request
  }
}
