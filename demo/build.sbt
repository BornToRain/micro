import Dependencies._

lazy val `demo-protocol` = (project in file("protocol"))
.settings(
  name := "demo-protocol",
  libraryDependencies ++= Seq(
    other.protobuf
  ),
  PB.targets in Compile := Seq(
    scalapb.gen() -> (sourceManaged in Compile).value
  )
)
.dependsOn(LocalProject("protocol"))

lazy val `demo-back`     = (project in file("back"))
.settings(
  name := "demo-back",
  libraryDependencies ++= Seq(
    akka.persistence, akka.persistenceQuery, akka.persistenceRxMongo,
    fp.catsCore,
    rxmongo.rxmongo, rxmongo.rxmongoStream,
    other.logback
  )
)
.dependsOn(`demo-protocol`, LocalProject("core"), LocalProject("http-support"))