package db

import scala.concurrent.ExecutionContext
import cats.effect._
import config.Config._
import doobie.hikari.HikariTransactor

object Database {

  def buildTransactor(config: DatabaseConfig, ec: ExecutionContext, blocker: Blocker)(implicit cs: ContextShift[IO]): Resource[IO, HikariTransactor[IO]] =
    HikariTransactor.newHikariTransactor[IO](
      config.driver,
      config.url,
      config.user,
      config.password,
      ec,
      blocker
    )

}
