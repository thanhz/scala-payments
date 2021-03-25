import cats.effect.{ExitCode, IO, IOApp}

object Runner extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {
    HttpServer.create()
  }
}
