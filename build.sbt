import Dependencies._

name                       := "micro"
version                    := "0.1"
scalaVersion  in ThisBuild := "2.12.6"
organization  in ThisBuild := "org.ryze.micro"
scalacOptions in ThisBuild := Seq(
  "-encoding", "UTF-8",
  "-source"  , "1.8",
  "-target"  , "1.8",
  "-Ypartial-unification"
)

//核心组件
lazy val `core`         = module("core")
.settings(
  libraryDependencies ++= Seq(
    akka.actor,
    akka.clusterShard
  )
)
.dependsOn(`protocol`)
//http组件
lazy val `http-support` = module("http-support")
.settings(
  libraryDependencies ++= Seq(
    akka.http, akka.stream,
    circe.parser, circe.genericExtras
  )
)
//协议
lazy val `protocol`     = module("protocol")
.settings(
  libraryDependencies ++= Seq(
    akka.clusterShard
  )
)
