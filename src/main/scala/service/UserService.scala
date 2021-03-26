package service

import cats.effect.IO
import fs2.Stream
import org.http4s.{Http, HttpRoutes, MediaType}
import org.http4s.dsl.Http4sDsl
import repository.UserRepository
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import model.User
import org.http4s.headers.`Content-Type`

class UserService(repository: UserRepository) extends Http4sDsl[IO] {
  implicit val userDecoder: Decoder[User] = deriveDecoder[User]
  implicit val userEncoder: Encoder[User] = deriveEncoder[User]
  implicit val userListEncoder: Encoder[List[User]] = deriveEncoder[List[User]]

  val rawJson: String =
    """
{
  "foo": "bar",
  "baz": 123,
  "list of stuff": [ 4, 5, 6 ]
}
"""

  val routes = HttpRoutes.of[IO] {
    case GET -> Root / "user" =>
      Ok(Stream("[") ++ repository.getUsers().map(_.asJson.noSpaces).intersperse(",") ++ Stream("]"), `Content-Type`(MediaType.application.json))

  }

}
