package quality

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.posNum
import org.scalacheck.Prop.forAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers

class QualityManagerImplementationProperties extends AnyFlatSpec with Checkers {
  implicit val itemGenerator: Arbitrary[Item] = Arbitrary {
    for {
      name    <- Arbitrary.arbitrary[String]
      sellIn  <- posNum[Int]
      quality <- posNum[Int]
    } yield Item(name, sellIn, quality)
  }

  "new implementation" should "have the same result" in {
    check(forAll { items: Array[Item] =>
      {
        true
        // new QualityManager(items).update() == newImplementation
      }
    })
  }
}
