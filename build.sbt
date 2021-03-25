name := "scala-payments"

version := "0.1"

scalaVersion := "2.13.5"

val http4sVersion = "0.21.18"
val pureConfig = "0.14.1"
val doobieVersion = "0.12.1"
val mySqlDriver = "8.0.23"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion,

  "com.github.pureconfig" %% "pureconfig" % pureConfig,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfig,

  "mysql" % "mysql-connector-java" % mySqlDriver,

  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-specs2" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,
)