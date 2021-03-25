package config

import cats.effect.{Blocker, ContextShift, IO, Resource}
import com.typesafe.config.ConfigFactory
import pureconfig.ConfigSource
import pureconfig.generic.auto._
import pureconfig.module.catseffect.syntax._

object Config {

  case class ServerConfig(host: String, port: Int)

  case class DatabaseConfig(driver: String, url: String, user: String, password: String, connectionPoolSize: Int)

  case class Config(server: ServerConfig, database: DatabaseConfig)

  def load(configFile: String = "application.conf")(implicit cs: ContextShift[IO]): Resource[IO, Config] = {
    Blocker[IO].flatMap { blocker =>
      Resource.liftF(ConfigSource.fromConfig(ConfigFactory.load(configFile)).loadF[IO, Config](blocker))
    }
  }
}
