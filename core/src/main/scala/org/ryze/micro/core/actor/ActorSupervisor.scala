package org.ryze.micro.core.actor

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Actor监管
  */
trait ActorSupervisor
{
  this: Actor with ActorLogging =>

  def get(name: String)                       = context child name
  def create(props: Props)(name: String)      = context actorOf(props, name)
  def getOrCreate(props: Props)(name: String) = get(name) getOrElse create(props)(name)
}
