import Dependencies._

name                       := "micro"
version                    := "0.1"
scalaVersion  in ThisBuild := "2.12.6"
organization  in ThisBuild := "org.ryze.micro"
scalacOptions in ThisBuild := Seq(
  "-encoding", "UTF-8",
  "-Ypartial-unification"
)

/*******************框架*******************/
//核心组件
lazy val `core`         = (project in file("core"))
.settings(
  libraryDependencies ++= Seq(
    akka.actor, akka.slf4j,
    akka.http, akka.stream,
    circe.parser, circe.genericExtras,
    akka.clusterShard, akka.clusterMetrics, akka.clusterTools
  )
)
.dependsOn(`protocol`)
//协议
lazy val `protocol`     = (project in file("protocol"))
.settings(
  libraryDependencies ++= Seq(
    akka.clusterShard
  )
)
/*******************框架*******************/

lazy val `demo`         = project in file("demo")
