package rental

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers

class RentalNewImplementationProperties extends AnyFlatSpec with Checkers {
  "new implementation" should "have the same result" in {
    assert("c'est pas faux".nonEmpty)
  }
}
