package quality

import scala.collection.mutable.ListBuffer

class QualityManager(val items: Array[Item]) {
  def update(): Array[Item] = {
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
