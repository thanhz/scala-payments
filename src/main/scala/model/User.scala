package model

case class User(id: Int, name: String, balance: Double)

trait UserErrors

case object UserNotFoundError extends UserErrors

case object InsufficientBalance extends UserErrors
