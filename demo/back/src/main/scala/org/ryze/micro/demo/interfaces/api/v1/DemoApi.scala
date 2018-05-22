package org.ryze.micro.demo.interfaces.api.v1

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import akka.pattern.ask
import org.ryze.micro.core.actor.ActorFactory
import org.ryze.micro.core.http.RestApi
import org.ryze.micro.demo.application.service.DemoService.Request.{Create, Get}
import org.ryze.micro.demo.interfaces.dto.DemoDTO
import org.ryze.micro.demo.protocol.{Delete, Update}

import scala.util.{Failure, Success}

class DemoApi(service: ActorRef)(implicit factory: ActorFactory) extends Actor with ActorLogging with RestApi
{
  import factory.runtime.{ec, materializer, timeout}

  private[this] val httpConfig = factory.config.getConfig("http")

  Http(context.system).bindAndHandle(route, httpConfig.getString("host"), httpConfig.getInt("port")) onComplete
  {
    case Success(d) => log.info(s"Demo模块启动成功: ${d.localAddress}")
    case Failure(e) => log.error(s"Demo模块启动失败: ${e.getMessage}")
  }

  override def route = logRequestResult(("demos", Logging.InfoLevel))
  {
    pathPrefix("demos")
    {
      pathEnd
      {
        (post & entity(as[Create]))
        {
          r => complete((service ? r).mapTo[String])
        } ~
        (put & entity(as[Update]))
        {
          r => onSuccess((service ? r).mapTo[Option[DemoDTO]])
          {
            case Some(d) => complete(d)
            case _       => complete(NotFound)
          }
        }
      } ~
      path(Segment)
      {
        id =>
        get
        {
          onSuccess((service ? Get(id)).mapTo[Option[DemoDTO]])
          {
            case Some(d) => complete(d)
            case _       => complete(NotFound)
          }
        } ~
        delete
        {
          complete
          {
            (service ? Delete(id)).mapTo[Boolean] map
            {
              case true => NoContent
              case _    => NotFound
            }
          }
        }
      }
    }
  }
  override def receive = Actor.emptyBehavior
}

object DemoApi
{
  final val NAME = "demo-api"

  def props(service: ActorRef)(implicit factory: ActorFactory) = Props(new DemoApi(service))
}
