package repository

import cats.effect.IO
import doobie.implicits.{toSqlInterpolator, _}
import doobie.util.transactor.Transactor
import fs2.Stream
import model.{IncorrectAmountError, Payment, PaymentErrors, PaymentNotFound}

class PaymentRepository(transactor: Transactor[IO]) {

  def getPayments(): Stream[IO, Payment] = {
    sql"SELECT * FROM payment"
      .query[Payment]
      .stream
      .transact(transactor)
  }

  def getPayment(id: Int): IO[Either[PaymentErrors, Payment]] = {
    sql"SELECT * FROM payment WHERE id = $id"
      .query[Payment]
      .option
      .transact(transactor)
      .map {
        case Some(payment) => Right(payment)
        case None => Left(PaymentNotFound)
      }
  }

  def updatePayment(id: Int, payment: Payment): IO[Either[PaymentErrors, Payment]] = {
    if (payment.amount < 0) {
      IO.pure(Left(IncorrectAmountError))
    } else {
      sql"UPDATE payment SET amount=${payment.amount} WHERE id = $id"
        .update
        .run
        .transact(transactor)
        .map {
          affectedRows =>
            if (affectedRows == 1) {
              Right(payment.copy(id = Some(id)))
            } else {
              Left(PaymentNotFound)
            }
        }
    }
  }

  def createPayment(payment: Payment): IO[Payment] = {
    sql"INSERT payment (sender, amount, receiver) VALUES (${payment.sender}, ${payment.amount}, ${payment.receiver})"
      .update
      .run //.withUniqueGeneratedKeys[Int]("id") "Not supported by MySQL
      .transact(transactor)
      .map {
        id => payment.copy(id = Some(id))
      }
  }

  def deletePayment(id: Long): IO[Either[PaymentErrors, Unit]] = {
    sql"DELETE FROM payment WHERE id = $id"
      .update
      .run
      .transact(transactor)
      .map {
        affectedRows =>
          if (affectedRows == 1) {
            Right(())
          } else {
            Left(PaymentNotFound)
          }
      }
  }
}
