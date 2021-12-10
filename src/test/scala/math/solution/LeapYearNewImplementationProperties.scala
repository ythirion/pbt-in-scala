package math.solution

import math.LeapYear
import org.scalacheck.Gen
import org.scalacheck.Prop.forAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers

class LeapYearNewImplementationProperties extends AnyFlatSpec with Checkers {
  private val years: Gen[Int] = Gen.choose(-2000, 4000)

  "new implementation" should "have the same result" in {
    check(
      forAll(years) { year =>
        {
          LeapYear.isLeapYear(year) == NewLeapYear.isLeapYear(year)
        }
      }
    )
  }
}
