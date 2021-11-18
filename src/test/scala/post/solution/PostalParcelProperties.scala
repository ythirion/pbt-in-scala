package post.solution

import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.Properties
import org.scalatest.OptionValues.convertOptionToValuable
import post.PostalParcel._

object PostalParcelProperties extends Properties("Postal Parcel") {
  property("delivery cost is max when weight > maxWeight") = forAll {
    weight: Double =>
      weight > maxWeight ==> {
        calculateDeliveryCosts(fromDouble(weight)).value == maxDeliveryCosts
      }
  }

  property("delivery cost is min when weight <= maxWeight") = forAll {
    weight: Double =>
      (weight > 0 && weight <= maxWeight) ==> {
        calculateDeliveryCosts(fromDouble(weight)).value == minDeliveryCosts
      }
  }

  property("delivery cost is None when weight <= 0") = forAll {
    weight: Double =>
      weight < 0 ==> {
        calculateDeliveryCosts(fromDouble(weight)).isEmpty
      }
  }
}
