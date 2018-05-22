package org.ryze.micro.demo.infrastructure

import org.ryze.micro.demo.domain.{Demo, DemoRepository}
import reactivemongo.api.DefaultDB
import reactivemongo.api.collections.bson.BSONCollection
import reactivemongo.bson.BSONDocument

import scala.concurrent.ExecutionContext

case class DemoRepositoryImpl(db: DefaultDB)(implicit ec: ExecutionContext) extends DemoRepository
{
  private[this] val collection = db[BSONCollection]("demo")

  private[this] def byId(id: String) = BSONDocument("_id" -> id)

  override def insert(d: Demo)       = collection insert d map (_.ok)
  override def update(d: Demo)       = collection update(byId(d._id), d) map (_.ok)
  override def selectOne(id: String) = collection.find(byId(id)).one[Demo]
  override def delete(id: String)    = collection remove byId(id) map (_.ok)
}
