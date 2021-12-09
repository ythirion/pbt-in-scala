package bank

final case class Account(
    balance: Double,
    isOverdraftAuthorized: Boolean,
    maxWithdrawal: Double,
    withdraws: List[Withdraw] = List.empty[Withdraw]
)
