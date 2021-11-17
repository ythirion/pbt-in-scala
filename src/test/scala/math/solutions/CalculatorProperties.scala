package math.solutions

import math.Calculator.add
import org.scalacheck.Prop.forAll
import org.scalacheck.Properties

object CalculatorProperties extends Properties("add") {
  property("commutativity") = forAll { (x: Int, y: Int) =>
    add(x, y) == add(y, x)
  }

  property("associativity") = forAll { (x: Int) =>
    add(add(x, 1), 1) == add(x, 2)
  }

  property("identity") = forAll { (x: Int) =>
    add(x, 0) == x
  }
}
