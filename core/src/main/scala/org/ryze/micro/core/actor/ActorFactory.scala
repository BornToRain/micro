package org.ryze.micro.core.actor

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

/**
  * 普通Actor工厂
  */
trait ActorFactory
{
  def get(name: String): Option[ActorRef]
  def create(props: Props)(name: String): ActorRef
  def getOrCreate(props: Props)(name: String) = get(name) getOrElse create(props)(name)
}

/**
  * Actor监管
  */
trait ActorSupervisor extends ActorFactory
{
  this: Actor with ActorLogging =>
  override def get(name: String) = context child name
  override def create(props: Props)(name: String) = context actorOf(props, name)
}
