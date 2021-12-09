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

import scala.collection.mutable.ListBuffer

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
        new QualityManager(items)
          .update() sameElements QualityManagerImplementationProperties
          .updateQuality(
            items
          )
      }
    })
  }
}

object QualityManagerImplementationProperties {
  private val agedBrie    = "Aged Brie"
  private val concertPass = "Backstage passes to a TAFKAL80ETC concert"
  private val sulfuras    = "Sulfuras, Hand of Ragnaros"

  def updateQuality(items: Array[Item]): Array[Item] = {
    val updatedItems = ListBuffer[Item]()

    for (i <- items.indices) {
      var updatedItem = items(i)

      if (
        !items(i).name.equals(agedBrie)
        && !items(i).name.equals(concertPass)
      ) {
        if (items(i).quality > 0) {
          if (!items(i).name.equals(sulfuras)) {
            updatedItem = updatedItem.copy(quality = updatedItem.quality - 1)
          }
        }
      } else {
        if (items(i).quality < 50) {
          updatedItem = updatedItem.copy(quality = updatedItem.quality + 1)

          if (items(i).name.equals(concertPass)) {
            if (items(i).sellIn < 11) {
              if (items(i).quality < 50) {
                updatedItem.copy(quality = updatedItem.quality + 1)
              }
            }

            if (items(i).sellIn < 6) {
              if (items(i).quality < 50) {
                updatedItem.copy(quality = updatedItem.quality + 1)
              }
            }
          }
        }
      }

      if (!items(i).name.equals(sulfuras)) {
        updatedItem = updatedItem.copy(sellIn = updatedItem.sellIn - 1)
      }

      if (items(i).sellIn < 0) {
        if (!items(i).name.equals(agedBrie)) {
          if (!items(i).name.equals(concertPass)) {
            if (items(i).quality > 0) {
              if (!items(i).name.equals(sulfuras)) {
                updatedItem = updatedItem.copy(sellIn = updatedItem.quality - 1)
              }
            }
          } else {
            updatedItem = updatedItem.copy(sellIn =
              updatedItem.quality - updatedItem.quality
            )
          }
        } else {
          if (items(i).quality < 50) {
            updatedItem = updatedItem.copy(sellIn = updatedItem.quality + 1)
          }
        }
      }
      updatedItems += updatedItem
    }
    updatedItems.toArray
  }

  private def updateItem(item: Item, addQuality: Int): Item =
    item.copy(quality = item.quality + addQuality)
}
