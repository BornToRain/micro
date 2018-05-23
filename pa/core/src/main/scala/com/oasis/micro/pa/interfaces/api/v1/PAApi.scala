package com.oasis.micro.pa.interfaces.api.v1

import akka.actor.{Actor, ActorLogging, Props}
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes._
import com.oasis.micro.pa.infrastructure.ConfigLoader
import com.oasis.micro.pa.infrastructure.service.{OasisClient, PAClient}
import com.oasis.micro.pa.interfaces.dto.PADTO
import com.oasis.micro.pa.protocol.Pay
import io.circe.Json
import io.circe.parser._
import org.ryze.micro.core.actor.ActorRuntime
import org.ryze.micro.core.http.RestApi

import scala.util.{Failure, Success}

class PAApi(client: PAClient, oasis: OasisClient)
(implicit runtime: ActorRuntime) extends Actor with ActorLogging with RestApi
{
  import runtime._

  Http().bindAndHandle(route, PAApi.host, PAApi.port) onComplete
  {
    case Success(d) => log.info(s"平安金管家模块启动成功: ${d.localAddress}")
    case Failure(e) => log.error(s"平安金管家模块启动失败: ${e.getMessage}")
  }

  override def route = logRequestResult(("pa", Logging.InfoLevel))
  {
    pathPrefix("v1" / "pa")
    {
      pathEnd
      {
        //入口
        (get & parameters('merchantCode, 'securityParam, 'securityKey))
        {
          (_, b, c) =>
          val data = parse(LASecurityUtils.decryptAESWithRSA(PAClient.publicKey, c, b)) getOrElse Json.Null
          log.info(s"data: $data")
          complete(data.hcursor.downField("user").as[PADTO.User] match
          {
            case Right(d) => oasis.registerByOpenId(d.openId)
            case _        => BadRequest -> """{"code": 0, "msg": "登录失败!"}"""
          })
        }
      } ~
      path("payments")
      {
        //订单回传
        (post & entity(as[Pay]))
        {
          r => complete
          {
            client upload r map
            {
              d => log.info(s"返回值: $d")
              Created
            }
          }
        }
      }
    }
  }
  override def receive = Actor.emptyBehavior
}

object PAApi extends ConfigLoader
{
  final val NAME = "pa-api"

  private[this] val httpConfig = loader.getConfig("http")

  def props(client: PAClient, oasis: OasisClient)(implicit runtime: ActorRuntime) = Props(new PAApi(client, oasis))

  lazy val host = httpConfig.getString("host")
  lazy val port = httpConfig.getInt("port")
}
