package rental

import java.time.LocalDate

final case class Rental(date: LocalDate, label: String, amount: Double)
