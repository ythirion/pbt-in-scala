package post

final case class PostalParcel(weight: Double)

object PostalParcel {
  val maxWeight        = 20.0
  val maxDeliveryCosts = 4.99
  val minDeliveryCosts = 1.99

  def fromDouble(weight: Double): Option[PostalParcel] =
    if (weight > 0) Some(PostalParcel(weight)) else None

  def calculateDeliveryCosts(
      postalParcel: Option[PostalParcel]
  ): Option[Double] = {
    postalParcel.map(p =>
      if (p.weight > maxWeight) maxDeliveryCosts else minDeliveryCosts
    )
  }
}
