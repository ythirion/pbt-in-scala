package math.solution

object NewLeapYear {
  def isLeapYear(year: Int): Boolean = {
    isTypicalLeapYear(year) &&
    (!isAnAtypicalCommonYear(year) || isAnAtypicalLeapYear(year))
  }

  private def isTypicalLeapYear(year: Int): Boolean = year % 4 == 0

  private def isAnAtypicalCommonYear(year: Int): Boolean = year % 100 == 0

  private def isAnAtypicalLeapYear(year: Int): Boolean = year % 400 == 0
}
