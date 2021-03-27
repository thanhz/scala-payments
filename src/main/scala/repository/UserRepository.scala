package repository

import cats.effect.IO
import doobie.implicits.{toSqlInterpolator, _}
import doobie.util.transactor.Transactor
import fs2.Stream
import model.{User, UserErrors, UserNotFoundError, InsufficientBalance}

class UserRepository(transactor: Transactor[IO]) {

  def getUsers(): Stream[IO, User] = {
    sql"SELECT * FROM user"
      .query[User]
      .stream
      .transact(transactor)
  }

  def getUser(id: Int): IO[Either[UserErrors, User]] = {
    sql"SELECT * FROM user WHERE id = $id"
      .query[User]
      .option
      .transact(transactor)
      .map {
        case Some(todo) => Right(todo)
        case None => Left(UserNotFoundError)
      }
  }

  def updateBalance(id: Int, user: User): IO[Either[UserErrors, User]] = {
    sql"UPDATE user SET balance=${user.balance} WHERE id = $id"
      .update
      .run
      .transact(transactor)
      .map {
        affectedRows =>
          if (affectedRows == 1) {
            Right(user.copy(id = id))
          } else {
            Left(UserNotFoundError)
          }
      }
  }
}
