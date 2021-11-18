package post.solution

import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalatest.OptionValues.convertOptionToValuable
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers
import post.PostalParcel._

class PostalParcelPropertiesFlatSpec extends AnyFlatSpec with Checkers {
  "delivery cost" should "be max when weight > maxWeight" in {
    check(forAll { weight: Double =>
      weight > maxWeight ==> {
        calculateDeliveryCosts(fromDouble(weight)).value == maxDeliveryCosts
      }
    })
  }

  "delivery cost" should "be min when weight <= maxWeight" in {
    check(forAll { weight: Double =>
      (weight > 0 && weight <= maxWeight) ==> {
        calculateDeliveryCosts(fromDouble(weight)).value == minDeliveryCosts
      }
    })
  }

  "delivery cost" should "be None when weight <= 0" in {
    check(forAll { weight: Double =>
      weight <= 0 ==> {
        calculateDeliveryCosts(fromDouble(weight)).isEmpty
      }
    })
  }
}
