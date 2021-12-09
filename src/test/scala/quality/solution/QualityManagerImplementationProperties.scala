package quality.solution

import org.scalacheck.Arbitrary
import org.scalacheck.Gen.posNum
import org.scalacheck.Prop.forAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.Checkers
import quality.{Item, QualityManager}

import scala.collection.mutable.ListBuffer

class QualityManagerImplementationProperties extends AnyFlatSpec with Checkers {
  implicit val itemGenerator: Arbitrary[Item] = Arbitrary {
    for {
      name    <- Arbitrary.arbitrary[String]
      sellIn  <- posNum[Int]
      quality <- posNum[Int]
    } yield Item(name, sellIn, quality)
  }

  "new implementation" should "have the same result" in {
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

    for (i <- items.indices) {
      if (!items(i).name.startsWith("a")) {
        if (items(i).quality > 0) {
          val item        = items(i)
          val updatedItem = item.copy(quality = items(i).quality - 1)
          updatedItems += updatedItem
        }
      } else {
        if (items(i).quality < 50) {
          val item        = items(i)
          val updatedItem = item.copy(quality = item.quality + 1)
          updatedItems += updatedItem

          if (items(i).name.length == 4) {
            if (items(i).sellIn < 11) {
              if (items(i).quality < 50) {
                val item        = items(i)
                val updatedItem = item.copy(quality = item.quality + 1)
                updatedItems += updatedItem
              }
            }

            if (items(i).sellIn < 6) {
              if (items(i).quality < 50) {
                val item        = items(i)
                val updatedItem = item.copy(quality = item.quality + 1)
                updatedItems += updatedItem
              }
            }
          }
        }
      }
    }
    updatedItems.toArray
  }
}
