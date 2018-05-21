package org.ryze.micro.demo.domain

import scala.concurrent.Future

trait DemoRepository
{
  def insert(d: Demo)      : Future[Boolean]
  def update(d: Demo)      : Future[Boolean]
  def selectOne(id: String): Future[Option[Demo]]
  def delete(id: String)   : Future[Boolean]
}
