import sbt._
import sbt.Keys._

object Version
{
  val akka  = "2.5.12"
  val http  = "10.1.1"
  val circe = "0.9.3"
  val cats  = "1.1.0"
}

object Dependencies
{
  object akka
  {
    lazy val actor              = apply("actor")
    lazy val slf4j              = apply("slf4j")
    lazy val stream             = apply("stream")
    //http
    lazy val http               = apply("http", Version.http)
    //集群
    lazy val cluster            = apply("cluster")
    lazy val clusterShard       = apply("cluster-sharding")
    lazy val clusterMetrics     = apply("cluster-metrics")
    //持久化
    lazy val persistence        = apply("persistence")
    lazy val persistenceQuery   = apply("persistence-query")

    @inline
    private[this] def apply(name: String, version: String = Version.akka) = "com.typesafe.akka" %% s"akka-$name" % version
  }

  object circe
  {
    lazy val parser        = apply("parser")
    lazy val genericExtras = apply("generic-extras")

    @inline
    private[this] def apply(name: String) = "io.circe" %% s"circe-$name" % Version.circe
  }

  object fp
  {
    lazy val catsCore = apply("core")
    lazy val catsFree = apply("free")

    @inline
    private[this] def apply(name: String) = "org.typelevel" %% s"cats-$name" % Version.cats
  }

  object other
  {
    lazy val logback  = "ch.qos.logback"       % "logback-classic"  % "1.2.3"
    lazy val protobuf = "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf"
  }

  def module(id: String) = Project(id, file(id))
  .settings(
    name       := id,
    moduleName := id
  )
}
