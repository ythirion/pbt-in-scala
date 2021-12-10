package rental.solution

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.posNum
import org.scalacheck.Prop.forAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers
import rental.{Rental, RentalCalculator}

import java.time.LocalDate

class RentalNewImplementationProperties extends AnyFlatSpec with Checkers {
  implicit val rentalGen: Arbitrary[Rental] = Arbitrary {
    for {
      date   <- Arbitrary.arbitrary[LocalDate]
      label  <- Arbitrary.arbitrary[String]
      amount <- posNum[Double]
    } yield Rental(date, label, amount)
  }

  "new implementation" should "have the same result" in {
    check(forAll { rentals: List[Rental] =>
      new RentalCalculator(rentals).calculateRental ==
        NewRentalStatementPrinter.print(rentals)
    })
  }
}
