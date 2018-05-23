package com.oasis.micro.pa.interfaces.dto

import io.circe.generic.JsonCodec

sealed trait PADTO

object PADTO
{
  //用户信息
  @JsonCodec
  case class User(openId: String, mobilePhone: Option[String], agent: Option[String], empType: Option[String], alias: Option[String],
    userType: Option[String]) extends PADTO
}
