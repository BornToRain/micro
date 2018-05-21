package org.ryze.micro.demo.infrastructure

import com.typesafe.config.Config
import reactivemongo.api.MongoDriver

import scala.concurrent.ExecutionContext

/**
  * MongoDB客户端
  */
case class MongoDBClient(config: Config)(implicit ec: ExecutionContext)
{
  private[this] val mongodbConfig = config.getConfig("mongodb")

  lazy val driver     = new MongoDriver
  lazy val connection = driver.connection(mongodbConfig.getString("server") :: Nil)
  lazy val db         = connection(mongodbConfig.getString("database"))
}