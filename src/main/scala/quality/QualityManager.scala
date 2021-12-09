package quality

import scala.collection.mutable.ListBuffer

class QualityManager(val items: Array[Item]) {
  def update(): Array[Item] = {
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
}
