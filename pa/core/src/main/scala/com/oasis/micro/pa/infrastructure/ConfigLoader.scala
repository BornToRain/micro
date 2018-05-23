package com.oasis.micro.pa.infrastructure

import com.typesafe.config.ConfigFactory

trait ConfigLoader
{
  lazy val loader = ConfigFactory.load()
}
