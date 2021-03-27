package service

import cats.effect.IO
import fs2.Stream
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import model.{User, UserErrors, UserNotFoundError}
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.`Content-Type`
import org.http4s.{HttpRoutes, MediaType}
import repository.UserRepository

class UserService(repository: UserRepository) extends Http4sDsl[IO] {
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]

  val routes = HttpRoutes.of[IO] {
    case GET -> Root / "user" =>
      Ok(Stream("[") ++ repository.getUsers().map(_.asJson.noSpaces).intersperse(",") ++ Stream("]"), `Content-Type`(MediaType.application.json))

    case GET -> Root / "user" / IntVar(id) =>
      for {
        user <- repository.getUser(id)
        result <- helper(user)
      } yield result

    case req @ PUT -> Root / "user" / IntVar(id) =>
      for {
        user <- req.as[User]
        update <- repository.updateBalance(id, user)
        result <- helper(update)
      } yield result

  }

  private def helper(result : Either[UserErrors, User]) = {
    result match {
      case Left(UserNotFoundError) => NotFound("User not Found")
      case Right(user) => Ok(user.asJson)
    }
  }

}
