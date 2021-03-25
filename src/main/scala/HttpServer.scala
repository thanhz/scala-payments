import cats.effect._
import config.Config
import db.Database
import doobie.{ExecutionContexts, Transactor}
import org.http4s.implicits.http4sKleisliResponseSyntaxOptionT
import org.http4s.server.blaze.BlazeServerBuilder
import service.HelloService

import scala.concurrent.ExecutionContext.Implicits.global

object HttpServer {
  implicit val cs: ContextShift[IO] = IO.contextShift(global)
  implicit val timer: Timer[IO] = IO.timer(global)

  val dependencies = for {
    config <- Config.load()
    ec <- ExecutionContexts.fixedThreadPool[IO](config.database.connectionPoolSize)
    blocker <- Blocker[IO]
    transactor <- Database.buildTransactor(config.database, ec, blocker)
  } yield (config.server, transactor)

  def create(): IO[ExitCode] = {
    dependencies.use {
      case (serverConfig, transactor) =>

        for {
          exitCode <- BlazeServerBuilder[IO](global)
            .bindHttp(serverConfig.port, serverConfig.host)
            .withHttpApp(new HelloService().routes.orNotFound)
            .serve
            .compile
            .drain.as(ExitCode.Success)
        } yield exitCode

    }
  }
}