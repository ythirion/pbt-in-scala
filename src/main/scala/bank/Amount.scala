package bank

final case class Amount(value: Double)

object Amount {
  def from(amount: Double): Option[Amount] =
    if (amount > 0) Some(Amount(amount)) else None
}
