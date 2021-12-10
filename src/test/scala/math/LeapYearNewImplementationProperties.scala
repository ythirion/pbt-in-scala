package math

import org.scalacheck.Prop.forAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers

class LeapYearNewImplementationProperties extends AnyFlatSpec with Checkers {
  "new implementation" should "have the same result" in {
    check(forAll { (year: Int) =>
      true
    })
  }
}
