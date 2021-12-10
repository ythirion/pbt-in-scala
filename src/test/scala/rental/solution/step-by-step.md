## Rental Calculator
* We duplicate the current implementation

```scala
class NewRentalCalculator(val rentals: List[Rental]) {
  private var _amount     = .0
  private var _calculated = false

  def amount(): Double = _amount

  def calculated(): Boolean = _calculated

  def calculateRental(): Either[String, String] = {
    if (rentals.isEmpty)
      Left("No rentals !!!")
    else {
      val result = new StringBuilder

      for (rental <- rentals) {
        if (!_calculated) this._amount += rental.amount
        result.append(formatLine(rental, _amount))
      }
      result.append(f"Total amount | ${this._amount}%.2f")
      _calculated = true

      Right(result.toString)
    }
  }

  private def formatLine(rental: Rental, amount: Double) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"
}
```

* We create the property that checks `f(x) == new_f(x)`

```scala
  "new implementation" should "have the same result" in {
    check(forAll { rentals: List[Rental] =>
      new RentalCalculator(rentals).calculateRental ==
        new NewRentalCalculator(rentals).calculateRental
    })
  }
```

* ScalaCheck needs to know how to generate instances of our case class `Rental`
  * Create an implicit rental generator

```scala
  implicit val rentalGen: Arbitrary[Rental] = Arbitrary {
    for {
      date   <- Arbitrary.arbitrary[LocalDate]
      label  <- Arbitrary.arbitrary[String]
      amount <- posNum[Double]
    } yield Rental(date, label, amount)
  }
```

* The property should succeed
  * Let's check the robustness of our property by introducing defect(s) in our new implementation
    * Mutate one or several lines
  * The test must fail : that's great our property is acting as a safety net for our refactoring

`Seeing a test failing is as important as seeing it passing`

### Step-by-step refactoring
* Identify code smells

```scala
// Why would we need to instantiate a new calculator with Rentals at each call ?'
// Not a calculator -> String as return
class NewRentalCalculator(val rentals: List[Rental]) {
  // var is evil
  private var _amount     = .0
  private var _calculated = false

  // Do not need a state to make a query
  def amount(): Double = _amount
  def calculated(): Boolean = _calculated

  // this function breaks the Command Query separation principle
  // Not a pure function
  def calculateRental(): Either[String, String] = {
    if (rentals.isEmpty)
      Left("No rentals !!!")
    else {
      // could be simplified by a fold
      val result = new StringBuilder

      for (rental <- rentals) {
        // mutation is evil
        // checking the state here...
        if (!_calculated) this._amount += rental.amount
        result.append(formatLine(rental, _amount))
      }
      result.append(f"Total amount | ${this._amount}%.2f")
      
      // mutation is evil
      _calculated = true

      Right(result.toString)
    }
  }

  // Unused amount
  private def formatLine(rental: Rental, amount: Double) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"
}
```

* Remove state / mutation
```scala
class NewRentalCalculator(val rentals: List[Rental]) {
  def calculateRental(): Either[String, String] = {
    if (rentals.isEmpty)
      Left("No rentals !!!")
    else {
      val result = new StringBuilder
      var amount = 0d

      for (rental <- rentals) {
        amount += rental.amount
        result.append(formatLine(rental, amount))
      }
      result.append(f"Total amount | ${amount}%.2f")

      Right(result.toString)
    }
  }

  private def formatLine(rental: Rental, amount: Double) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"
}
```

* Remove amount

```scala
class NewRentalCalculator(val rentals: List[Rental]) {
  def calculateRental(): Either[String, String] = {
    if (rentals.isEmpty)
      Left("No rentals !!!")
    else {
      val result = new StringBuilder

      for (rental <- rentals) {
        result.append(formatLine(rental))
      }
      result.append(f"Total amount | ${rentals.map(_.amount).sum}%.2f")

      Right(result.toString)
    }
  }

  private def formatLine(rental: Rental) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"
}
```

* Use fold to simplify iteration on rentals

```scala
class NewRentalCalculator(val rentals: List[Rental]) {
  def calculateRental(): Either[String, String] = {
    if (rentals.isEmpty) Left("No rentals !!!")
    else Right(statementFrom(rentals) + formatTotal(rentals))
  }

  private def statementFrom(rentals: List[Rental]): String =
    rentals.foldLeft("")((statement, rental) => statement + formatLine(rental))

  private def formatLine(rental: Rental) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"

  private def formatTotal(rentals: List[Rental]): String =
    f"Total amount | ${rentals.map(_.amount).sum}%.2f"
}
```

* Change Calculator to object / use better names

```scala
object NewRentalStatementPrinter {
  def print(rentals: List[Rental]): Either[String, String] = {
    if (rentals.isEmpty) Left("No rentals !!!")
    else Right(statementFrom(rentals) + formatTotal(rentals))
  }

  private def statementFrom(rentals: List[Rental]): String =
    rentals.foldLeft("")((statement, rental) => statement + formatLine(rental))

  private def formatLine(rental: Rental) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"

  private def formatTotal(rentals: List[Rental]): String =
    f"Total amount | ${rentals.map(_.amount).sum}%.2f"
}
```

* Adapt the call in the property as well

```scala
  "new implementation" should "have the same result" in {
    check(forAll { rentals: List[Rental] =>
      new RentalCalculator(rentals).calculateRental ==
        NewRentalStatementPrinter.print(rentals)
    })
  }
```

We have now a new better implementation without having introduced regression and without having spent a lot of time to identify test cases and written those