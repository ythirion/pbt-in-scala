package quality

import scala.collection.mutable.ListBuffer

class QualityManager(val items: Array[Item]) {
  def update(): Array[Item] = {
    val updatedItems = ListBuffer[Item]()

    for (i <- 0 until items.length) {
      var updatedQuality = items(i).quality
      var updatedSellIn  = items(i).sellIn

      if (
        !items(i).name.equals("Aged Brie")
        && !items(i).name.equals("Backstage passes to a TAFKAL80ETC concert")
      ) {
        if (updatedQuality > 0) {
          if (!items(i).name.equals("Sulfuras, Hand of Ragnaros")) {
            updatedQuality -= 1
          }
        }
      } else {
        if (updatedQuality < 50) {
          updatedQuality += 1

          if (
            items(i).name.equals("Backstage passes to a TAFKAL80ETC concert")
          ) {
            if (items(i).sellIn < 11) {
              if (updatedQuality < 50) {
                updatedQuality += 1
              }
            }

            if (items(i).sellIn < 6) {
              if (updatedQuality < 50) {
                updatedQuality += 1
              }
            }
          }
        }
      }

      if (!items(i).name.equals("Sulfuras, Hand of Ragnaros")) {
        updatedSellIn -= 1
      }

      updatedItems += items(i)
        .copy(quality = updatedQuality, sellIn = updatedSellIn)
    }
    updatedItems.toArray
  }
}
