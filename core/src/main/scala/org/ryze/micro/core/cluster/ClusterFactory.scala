package org.ryze.micro.core.cluster

import java.util.Optional

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.client.ClusterClientReceptionist
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings}
import akka.cluster.sharding.ShardRegion.HashCodeMessageExtractor
import org.ryze.micro.protocol.domain.Command

/**
  * 集群工厂
  */
abstract class ClusterFactory(cc: ClusterConfiguration)(implicit system: ActorSystem)
{
  //创建分片
  def createShard(name: String)(props: Props): ActorRef
  //创建代理
  def createProxy(name: String)              : ActorRef
  //获取分片
  def get(name: String)                      : Option[ActorRef]
  //查询分片,无则创建.并注册客户端
  def shard(name: String)(props: Props)              =
  {
    val ref = get(name) getOrElse createShard(name)(props)
    registerClientReceptionist(name)
    ref
  }
  //查询代理,无则创建,并注册客户端
  def proxy(name: String)                            =
  {
    val ref = get(name) getOrElse createProxy(name)
    registerClientReceptionist(name)
    ref
  }
  //注册客户端
  def registerClientReceptionist(name: String): Unit = ClusterClientReceptionist(system) registerService get(name).get
}

class DefaultClusterFactory(cc: ClusterConfiguration)(implicit system: ActorSystem) extends ClusterFactory(cc)
{
  override def createShard(name: String)(props: Props) = ClusterSharding(system) start(
    typeName         = name,
    entityProps      = props,
    settings         = ClusterShardingSettings(system).withRole(name),
    messageExtractor = cc.messageExtractor
  )
  override def createProxy(name: String)               = ClusterSharding(system) startProxy(
    typeName = name,
    role = Optional.of(name),
    messageExtractor = cc.messageExtractor
  )
  override def get(name: String) = try
  {
    Some(ClusterSharding(system) shardRegion name)
  }
  catch
  {
    case _: Exception => None
  }
}

case class ClusterConfiguration(maxShard: Int)
{
  //Hash分片
  lazy val messageExtractor = new HashCodeMessageExtractor(maxShard)
  {
    override def entityId(message: Any) = message match
    {
      case c: Command => c.id
    }
  }
}
