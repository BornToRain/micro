package org.ryze.micro.core.actor

import akka.actor.{Actor, ActorLogging, Props}

/**
  * Actor监管
  */
trait ActorSupervisor
{
  this: Actor with ActorLogging =>

  @inline
  def get(name: String)                       = context child name
  @inline
  def create(props: Props)(name: String)      = context actorOf(props, name)
  @inline
  def getOrCreate(props: Props)(name: String) = get(name) getOrElse create(props)(name)
}
