package service

import cats.effect.IO
import fs2.Stream
import io.circe._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.syntax._
import model._
import org.http4s.circe.CirceEntityCodec.circeEntityDecoder
import org.http4s.circe.jsonEncoder
import org.http4s.dsl.Http4sDsl
import org.http4s.headers.{Location, `Content-Type`}
import org.http4s.{HttpRoutes, MediaType, Uri}
import repository.PaymentRepository

class PaymentService(repository: PaymentRepository) extends Http4sDsl[IO] {
  implicit val paymentDecoder: Decoder[Payment] = deriveDecoder[Payment]
  implicit val paymentEncoder: Encoder[Payment] = deriveEncoder[Payment]

  val routes = HttpRoutes.of[IO] {
    case GET -> Root / "payment" =>
      Ok(Stream("[") ++ repository.getPayments().map(_.asJson.noSpaces).intersperse(",") ++ Stream("]"), `Content-Type`(MediaType.application.json))

    case GET -> Root / "payment" / IntVar(id) =>
      for {
        user <- repository.getPayment(id)
        result <- helper2(user)
      } yield result

    case req @ PUT -> Root / "payment" / IntVar(id) =>
      for {
        payment <- req.as[Payment]
        update <- repository.updatePayment(id, payment)
        result <- helper2(update)
      } yield result

    case req @ POST -> Root / "payment" =>
      for {
        payment <- req.as[Payment]
        createdPayment <- repository.createPayment(payment)
        response <- Created(createdPayment.asJson, Location(Uri.unsafeFromString(s"/payment/${createdPayment.id.get}")))
      } yield response

  }

  private def helper2(result: Either[UserErrors, Payment]) = {
    result match {
      case Left(IncorrectAmountError) => NotFound(IncorrectAmountError.getMsg)
      case Left(UserNotFoundError) => NotFound(UserNotFoundError.getMsg)
      case Right(payment) => Ok(payment.asJson)
    }
  }

}
