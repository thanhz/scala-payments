import cats.effect.IO
import fs2.Stream
import io.circe.Json
import io.circe.literal._
import model.Payment
import org.http4s.circe._
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.{Request, Response, Status, Uri}
import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import repository.PaymentRepository
import service.PaymentService

class PaymentTestSpec extends AnyWordSpec with MockFactory with Matchers {
  private val repository = stub[PaymentRepository]
  private val service = new PaymentService(repository).routes


  "PaymentService" should {
    "get all payments" in {
      val payment1 = Payment(Some(1), "SND1", 100, "REC1")
      val payment2 = Payment(Some(1), "SND2", 200, "REC2")
      val payments = Stream(payment1, payment2)

      (() => repository.getPayments()).when().returns(payments)

      val response = serve(Request[IO](GET, uri"/payment"))
      response.status shouldBe Status.Ok
      response.as[Json].unsafeRunSync() shouldBe
        json"""
        [
          {
            "id": ${payment1.id},
            "sender": ${payment1.sender},
            "amount": ${payment1.amount},
            "receiver": ${payment1.receiver}
          },
          {
            "id": ${payment2.id},
            "sender": ${payment2.sender},
            "amount": ${payment2.amount},
            "receiver": ${payment2.receiver}
          }
        ]"""
    }

    "return a single payment" in {
      val id = 1
      val payment1 = Payment(Some(id), "SND1", 100, "REC1")
      (repository.getPayment _).when(id).returns(IO.pure(Right(payment1)))

      val response = serve(Request[IO](GET, Uri.unsafeFromString(s"/payment/$id")))
      response.status shouldBe Status.Ok
      response.as[Json].unsafeRunSync() shouldBe
        json"""
         {
           "id": ${payment1.id},
           "sender": ${payment1.sender},
           "amount": ${payment1.amount},
           "receiver": ${payment1.receiver}
         }"""
    }

    "update a payment" in {
      val id = 1
      val payment1 = Payment(Some(id), "SND1", 100, "REC1")
      (repository.updatePayment _).when(id, payment1).returns(IO.pure(Right(payment1.copy(id = Some(id)))))
      val updateJson =
        json"""
         {
           "id": ${payment1.id},
           "sender": ${payment1.sender},
           "amount": ${payment1.amount},
           "receiver": ${payment1.receiver}
         }"""

      val response = serve(Request[IO](PUT, Uri.unsafeFromString(s"/payment/$id")).withEntity(updateJson))
      response.status shouldBe Status.Ok
      response.as[Json].unsafeRunSync() shouldBe
        json"""
         {
           "id": ${payment1.id},
           "sender": ${payment1.sender},
           "amount": ${payment1.amount},
           "receiver": ${payment1.receiver}
         }"""
    }
  }

  private def serve(request: Request[IO]): Response[IO] = {
    service.orNotFound(request).unsafeRunSync()
  }
}
