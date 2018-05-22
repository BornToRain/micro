package org.ryze.micro.demo.domain

case class Demo(_id: String, name: String)

object Demo
{
  import reactivemongo.bson.Macros

  implicit val bson = Macros.handler[Demo]
}