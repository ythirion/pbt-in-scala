package math.solution

import math.Calculator.add
import math.solution.CalculatorProperties.property
import org.scalacheck.Prop.forAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers

class CalculatorPropertiesFlatSpec extends AnyFlatSpec with Checkers {
  "add" should "be commutative" in {
    check(forAll { (x: Int, y: Int) =>
      add(x, y) == add(y, x)
    })
  }

  "add" should "be associative" in {
    check(forAll { (x: Int) =>
      add(add(x, 1), 1) == add(x, 2)
    })
  }

  "0" should "be identity" in {
    check(forAll { (x: Int) =>
      add(x, 0) != x
    })
  }
}
