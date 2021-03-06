package bank

object AccountService {
  def withdraw(account: Account, command: Withdraw): Either[String, Account] = {
    if (hasAlreadyBeenApplied(account, command))
      Right(account)
    else
      applyWithdraw(account, command)
  }

  private def applyWithdraw(
      account: Account,
      command: Withdraw
  ): Either[String, Account] = {
    command.amount.value match {
      case amount if exceedMaxWithdrawal(account, amount) =>
        Left(s"Amount exceeding your limit of ${account.maxWithdrawal}")
      case amount if exceedBalance(account, amount) =>
        Left(s"Insufficient balance to withdraw : $amount")
      case _ =>
        Right(
          account.copy(
            balance = account.balance - command.amount.value,
            withdraws = account.withdraws :+ command
          )
        )
    }
  }

  private def hasAlreadyBeenApplied(account: Account, command: Withdraw) =
    account.withdraws.contains(command)

  private def exceedMaxWithdrawal(
      account: Account,
      withdrawAmount: Double
  ): Boolean =
    withdrawAmount >= account.maxWithdrawal

  private def exceedBalance(account: Account, withdrawAmount: Double): Boolean =
    withdrawAmount > account.balance && !account.isOverdraftAuthorized
}
