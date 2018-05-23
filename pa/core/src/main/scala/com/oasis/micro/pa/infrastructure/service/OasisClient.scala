package com.oasis.micro.pa.infrastructure.service

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, RequestEntity, Uri}
import akka.http.scaladsl.unmarshalling.Unmarshal
import com.oasis.micro.pa.infrastructure.ConfigLoader
import com.oasis.micro.pa.infrastructure.service.OasisClient.Response.Register
import org.ryze.micro.core.actor.ActorRuntime
import org.ryze.micro.core.http.JsonSupport

import scala.concurrent.Future

/**
  * 泓华客户端
  */
case class OasisClient(implicit runtime: ActorRuntime) extends JsonSupport
{
  import runtime._

  private[this] def post(uri: String)(entity: Future[RequestEntity]) = for
  {
    request  <- entity
    response <- Http().singleRequest(HttpRequest(HttpMethods.POST, Uri(s"${OasisClient.gateway}$uri"), entity = request))
  } yield response

  /**
    * openId注册泓华账号
    */
  def registerByOpenId(openId: String) = for
  {
    response <- post("/userAccount/third/register")(Marshal(Map("openId" -> openId)).to[RequestEntity])
    body     <- Unmarshal(response).to[Register]
  } yield body
}

object OasisClient extends ConfigLoader
{
  private[this] lazy val oasisConfig = loader.getConfig("oasis")

  //泓华网关地址
  lazy val gateway = oasisConfig.getString("gateway")

  object Response
  {
    //注册用户返回
    case class Register(accountId: String, openId: String)
  }
}