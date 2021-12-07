package bank.solution

import bank.{Account, Withdraw}

final case class AccountBuilder(
    balance: Double = 0,
    isOverdraftAuthorized: Boolean = false,
    maxWithdrawal: Double = 0
) {
  def withEnoughMoney(command: Withdraw): AccountBuilder =
    copy(balance = command.amount.value + 1)

  def withdrawAmountReachingMaxWithdrawal(command: Withdraw): AccountBuilder =
    copy(maxWithdrawal = command.amount.value - 1)

  def withoutReachingMaxWithdrawal(command: Withdraw): AccountBuilder =
    copy(maxWithdrawal = command.amount.value + 1)

  def withInsufficientBalance(command: Withdraw): AccountBuilder =
    copy(balance = command.amount.value - 1)

  def withoutOverDraftAuthorized(): AccountBuilder =
    copy(isOverdraftAuthorized = false)

  def withOverDraftAuthorized(): AccountBuilder =
    copy(isOverdraftAuthorized = true)

  def build(): Account =
    Account(balance, isOverdraftAuthorized, maxWithdrawal)
}
