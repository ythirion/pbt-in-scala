package rental.solution

import rental.Rental

import java.lang.System.lineSeparator

object NewRentalStatementPrinter {
  def print(rentals: List[Rental]): Either[String, String] = {
    if (rentals.isEmpty) Left("No rentals !!!")
    else Right(statementFrom(rentals) + formatTotal(rentals))
  }

  private def statementFrom(rentals: List[Rental]): String =
    rentals.foldLeft("")((statement, rental) => statement + formatLine(rental))

  private def formatLine(rental: Rental) =
    f"${rental.date} : ${rental.label} | ${rental.amount}%.2f${lineSeparator()}"

  private def formatTotal(rentals: List[Rental]): String =
    f"Total amount | ${rentals.map(_.amount).sum}%.2f"
}
