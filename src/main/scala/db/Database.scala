package db

import cats.effect._
import config.Config._
import doobie.hikari.HikariTransactor
import org.flywaydb.core.Flyway

import scala.concurrent.ExecutionContext

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

  def setupDB(transactor: HikariTransactor[IO]): IO[Unit] = {
    transactor.configure { dataSource =>
      IO {
        val flyWay = Flyway.configure().dataSource(dataSource).load()
        //flyWay.baseline() Uncomment on first use to create schema history table
        flyWay.migrate()
      }
    }
  }

}
