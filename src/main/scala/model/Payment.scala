package model

case class Payment(id: Option[Int], sender: String, amount: Double, receiver: String)

trait UserErrors {
  def getMsg: String
}

case object UserNotFoundError extends UserErrors {
  override def getMsg: String = "Payment not found"
}

case object IncorrectAmountError extends UserErrors {
  override def getMsg: String = "payment can't be less than 0"
}
