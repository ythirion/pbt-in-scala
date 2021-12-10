## Postal Parcel
* Which kind of properties could we identify ?
```scala
object PostalParcel {
  val maxWeight        = 20.0
  val maxDeliveryCosts = 4.99
  val minDeliveryCosts = 1.99

  def fromDouble(weight: Double): Option[PostalParcel] =
    if (weight > 0) Some(PostalParcel(weight)) else None

  def calculateDeliveryCosts(
      postalParcel: Option[PostalParcel]
  ): Option[Double] = {
    postalParcel.map(p =>
      if (p.weight > maxWeight) maxDeliveryCosts else minDeliveryCosts
    )
  }
}
```
* for all `weight` <= 0 `delivery cost` should be None
* for all `weight` > maxWeight `delivery cost` should be max
* for all `weight` <= maxWeight `delivery cost` should be min

* To check our properties we need to either :
  * Filter our inputs with pre-condition :

```scala
  "delivery cost" should "be max when weight > maxWeight" in {
    check(forAll { weight: Double =>
      weight > maxWeight ==> {
        calculateDeliveryCosts(fromDouble(weight)).value == maxDeliveryCosts
      }
    })
  }
```

* Or create custom generator

```scala
  private val minWeightGenerator = Gen.choose(0.01, maxWeight)

  "delivery cost" should "be min when weight <= maxWeight" in {
    check(forAll(minWeightGenerator) { weight: Double =>
      calculateDeliveryCosts(fromDouble(weight)).value == minDeliveryCosts
    })
  }
```

* Let's use pre-conditions for this example :

```scala
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
```