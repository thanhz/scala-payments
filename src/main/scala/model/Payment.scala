package model

case class Payment(id: Option[Int], sender: String, amount: Double, receiver: String)

trait PaymentErrors {
  def getMsg: String
}

case object PaymentNotFound extends PaymentErrors {
  override def getMsg: String = "Payment not found"
}

case object IncorrectAmountError extends PaymentErrors {
  override def getMsg: String = "payment can't be less than 0"
}
