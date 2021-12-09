package quality.solution

import org.scalacheck.Prop.forAll
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers
import quality.{Item, QualityManager}

import scala.collection.mutable.ListBuffer

class QualityManagerImplementationProperties extends AnyFlatSpec with Checkers {
  implicit val itemGenerator: Arbitrary[Item] = Arbitrary {
    for {
      name <- Gen.oneOf(
        "Aged Brie",
        "Backstage passes to a TAFKAL80ETC concert",
        "Sulfuras, Hand of Ragnaros",
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
  def updateQuality(items: Array[Item]): Array[Item] = {

    val updatedItems = ListBuffer[Item]()

    for (i <- 0 until items.length) {
      var updatedItem = items(i)

      if (
        !items(i).name.equals("Aged Brie")
        && !items(i).name.equals("Backstage passes to a TAFKAL80ETC concert")
      ) {
        if (items(i).quality > 0) {
          if (!items(i).name.equals("Sulfuras, Hand of Ragnaros")) {
            updatedItem = updatedItem.copy(quality = updatedItem.quality - 1)
          }
        }
      } else {
        if (items(i).quality < 50) {
          updatedItem = updatedItem.copy(quality = updatedItem.quality + 1)

          if (
            items(i).name.equals("Backstage passes to a TAFKAL80ETC concert")
          ) {
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

      if (!items(i).name.equals("Sulfuras, Hand of Ragnaros")) {
        updatedItem = updatedItem.copy(sellIn = updatedItem.sellIn - 1)
      }

      if (items(i).sellIn < 0) {
        if (!items(i).name.equals("Aged Brie")) {
          if (
            !items(i).name.equals("Backstage passes to a TAFKAL80ETC concert")
          ) {
            if (items(i).quality > 0) {
              if (!items(i).name.equals("Sulfuras, Hand of Ragnaros")) {
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
