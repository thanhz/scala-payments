name := "scala-payments"

version := "0.1"

scalaVersion := "2.13.5"

lazy val http4sVersion = "0.21.18"
lazy val pureConfig = "0.14.1"
lazy val doobieVersion = "0.12.1"
lazy val mySqlDriver = "8.0.23"
lazy val circeVersion = "0.13.0"
lazy val scalaTestVersion = "3.2.5"
lazy val scalaMockVersion = "5.1.0"
lazy val logBackVersion = "1.2.3"
lazy val flywayDbVersion = "7.7.1"

libraryDependencies ++= Seq(
  "org.http4s" %% "http4s-dsl" % http4sVersion,
  "org.http4s" %% "http4s-blaze-server" % http4sVersion,
  "org.http4s" %% "http4s-blaze-client" % http4sVersion % "it,test",
  "org.http4s" %% "http4s-circe" % http4sVersion,

  "com.github.pureconfig" %% "pureconfig" % pureConfig,
  "com.github.pureconfig" %% "pureconfig-cats-effect" % pureConfig,

  "mysql" % "mysql-connector-java" % mySqlDriver,

  "org.tpolecat" %% "doobie-core" % doobieVersion,
  "org.tpolecat" %% "doobie-hikari" % doobieVersion,

  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-literal" % circeVersion % "test",

  "org.scalatest" %% "scalatest" % scalaTestVersion % "test",
  "org.scalamock" %% "scalamock" % scalaMockVersion % "test",

  "ch.qos.logback" % "logback-classic" % logBackVersion,

  "org.flywaydb" % "flyway-core" % flywayDbVersion
)