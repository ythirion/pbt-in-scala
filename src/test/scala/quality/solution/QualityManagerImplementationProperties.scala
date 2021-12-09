package quality.solution

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers
import quality.solution.QualityManagerImplementationProperties.{
  agedBrie,
  concertPass,
  sulfuras
}
import quality.{Item, QualityManager}

class QualityManagerImplementationProperties extends AnyFlatSpec with Checkers {
  implicit val itemGenerator: Arbitrary[Item] = Arbitrary {
    for {
      name <- Gen.oneOf(
        agedBrie,
        concertPass,
        sulfuras,
        "Other item name"
      )
      sellIn  <- Arbitrary.arbitrary[Int]
      quality <- Arbitrary.arbitrary[Int]
    } yield Item(name, sellIn, quality)
  }

  "new implementation" should "have the same result than the legacy one" in {
    check(forAll { items: Array[Item] =>
      {
        val oldImplementationResults = new QualityManager(items).update()
        val newImplementationResults =
          QualityManagerImplementationProperties.updateQuality(items)

        print(oldImplementationResults, newImplementationResults)

        oldImplementationResults sameElements newImplementationResults
      }
    })
  }

  private def print(oldImp: Array[Item], newImp: Array[Item]): Unit = {
    for (i <- oldImp.indices) {
      println(s"${oldImp(i)} / ${newImp(i)} -> ${oldImp(i) == newImp(i)}}")
    }
  }
}

object QualityManagerImplementationProperties {
  private val agedBrie    = "Aged Brie"
  private val concertPass = "Backstage passes to a TAFKAL80ETC concert"
  private val sulfuras    = "Sulfuras, Hand of Ragnaros"

  def updateQuality(items: Array[Item]): Array[Item] = {
    items.map { item =>
      var updatedQuality = item.quality
      var updatedSellIn  = item.sellIn

      if (
        !item.name.equals(agedBrie)
        && !item.name.equals(concertPass)
      ) {
        if (updatedQuality > 0) {
          if (!item.name.equals(sulfuras)) {
            updatedQuality -= 1
          }
        }
      } else {
        if (updatedQuality < 50) {
          updatedQuality += 1

          if (item.name.equals(concertPass)) {
            if (item.sellIn < 11) {
              if (updatedQuality < 50) {
                updatedQuality += 1
              }
            }

            if (item.sellIn < 6) {
              if (updatedQuality < 50) {
                updatedQuality += 1
              }
            }
          }
        }
      }

      if (!item.name.equals(sulfuras)) {
        updatedSellIn -= 1
      }

      item.copy(quality = updatedQuality, sellIn = updatedSellIn)
    }
  }
}
