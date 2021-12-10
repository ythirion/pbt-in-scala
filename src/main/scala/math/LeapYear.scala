package math

object LeapYear {
  def isLeapYear(year: Int): Boolean = {
    var isLeap = false

    if (year % 4 == 0) {
      if (year % 100 == 0) {
        if (year % 400 == 0)
          isLeap = true
        else
          isLeap = false
      } else
        isLeap = true
    } else {
      isLeap = false
    }
    isLeap
  }
}
