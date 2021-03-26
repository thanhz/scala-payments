package repository

import cats.effect.IO
import doobie.implicits.{toSqlInterpolator, _}
import doobie.util.transactor.Transactor
import fs2.Stream
import model.User

class UserRepository(transactor: Transactor[IO]) {

  def getUsers(): Stream[IO, User] = {
    sql"SELECT * FROM user"
      .query[User]
      .stream
      .transact(transactor)
  }
}
