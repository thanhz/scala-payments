import cats.effect.IO
import fs2.Stream
import io.circe.Json
import io.circe.literal._
import model.User
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{Request, Response, Status, Uri, _}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repository.UserRepository
import service.UserService

class UserTestSpec extends AnyWordSpec with MockFactory with Matchers {
  private val repository = stub[UserRepository]
  private val service = new UserService(repository).routes


  "UserService" should {
    "get all users" in {
      val user1 = User(1, "Bob", 999)
      val user2 = User(2, "Alice", 100)
      val users = Stream(user1, user2)

      (() => repository.getUsers()).when().returns(users)

      val response = serve(Request[IO](GET, uri"/user"))
      response.status shouldBe Status.Ok
      response.as[Json].unsafeRunSync() shouldBe
        json"""
        [
         {
           "id": ${user1.id},
           "name": ${user1.name},
           "balance": ${user1.balance}
         },
         {
           "id": ${user2.id},
           "name": ${user2.name},
           "balance": ${user2.balance}
         }
        ]"""
    }

    "return a single user" in {
      val id = 1
      val user1 = User(id, "Bob", 999)
      (repository.getUser _).when(id).returns(IO.pure(Right(user1)))

      val response = serve(Request[IO](GET, Uri.unsafeFromString(s"/user/$id")))
      response.status shouldBe Status.Ok
      response.as[Json].unsafeRunSync() shouldBe
        json"""
         {
           "id": ${user1.id},
           "name": ${user1.name},
           "balance": ${user1.balance}
         }"""
    }

    "update a user" in {
      val id = 1
      val user1 = User(id, "Bob", 999)
      (repository.updateBalance _).when(id, user1).returns(IO.pure(Right(user1.copy(id = id))))
      val updateJson =
        json"""
         {
           "id": ${user1.id},
           "name": ${user1.name},
           "balance": ${user1.balance}
         }"""

      val response = serve(Request[IO](PUT, Uri.unsafeFromString(s"/user/$id")).withEntity(updateJson))
      response.status shouldBe Status.Ok
      response.as[Json].unsafeRunSync() shouldBe
        json"""
         {
           "id": ${user1.id},
           "name": ${user1.name},
           "balance": ${user1.balance}
         }"""
    }
  }

  private def serve(request: Request[IO]): Response[IO] = {
    service.orNotFound(request).unsafeRunSync()
  }
}
