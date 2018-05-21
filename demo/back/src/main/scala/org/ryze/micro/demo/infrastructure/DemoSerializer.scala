package org.ryze.micro.demo.infrastructure

import akka.serialization.SerializerWithStringManifest
import org.ryze.micro.demo.domain.event.{Created, Deleted, Updated}

/**
  * Demo事件序列化(protobuf)
  */
class DemoSerializer extends SerializerWithStringManifest
{
  final val CREATED = classOf[Created].getName
  final val UPDATED = classOf[Updated].getName
  final val DELETED = classOf[Deleted].getName

  override def identifier = getClass.getName.hashCode
  override def manifest(o: AnyRef) = o.getClass.getName
  override def toBinary(o: AnyRef) = o match
  {
    case e: Created => e.toByteArray
    case e: Updated => e.toByteArray
    case e: Deleted => e.toByteArray
  }
  override def fromBinary(bytes: Array[Byte], manifest: String) = manifest match
  {
    case CREATED => Created.parseFrom(bytes)
    case UPDATED => Updated.parseFrom(bytes)
    case DELETED => Deleted.parseFrom(bytes)
  }
}
