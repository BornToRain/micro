package org.ryze.micro.protocol.cluster

import akka.cluster.sharding.ShardRegion.HashCodeMessageExtractor
import org.ryze.micro.protocol.domain.Command

/**
  * Hash分片
  */
case class MessageExtractor(maxShards: Int) extends HashCodeMessageExtractor(maxShards)
{
  override def entityId(message: Any) = message match
  {
    case c: Command => c.id
  }
}
