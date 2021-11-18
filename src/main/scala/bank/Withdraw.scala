package bank

import java.time.LocalDate
import java.util.UUID

final case class Withdraw(
    clientId: UUID,
    amount: Amount,
    requestDate: LocalDate
)
