import Dependencies._

//平安金管家协议(请求|命令|事件)
lazy val `pa-protocol` = (project in file ("protocol"))
.settings(
                  name := "pa-protocol",
  libraryDependencies ++= Seq(
    other.protobuf
  ),
  PB.targets in Compile := Seq(
    scalapb.gen() -> (sourceManaged in Compile).value
  )
)
.dependsOn(LocalProject("protocol"))

//平安金管家核心实现
lazy val `pa-core`     = (project in file ("core"))
.settings(
  name                 := "pa-core",
  libraryDependencies ++= Seq(
    akka.persistence, akka.persistenceQuery, akka.persistenceRxMongo,
    fp.catsCore,
    rxmongo.rxmongo, rxmongo.rxmongoStream,
    other.logback
  )
)
.dependsOn(`pa-protocol`, LocalProject("core"))