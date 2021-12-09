package bank.solution

import bank.{Account, Withdraw}
import org.scalacheck.Gen.posNum

final case class AccountBuilder(
    balance: Double = 0,
    isOverdraftAuthorized: Boolean = false,
    maxWithdrawal: Double = 0
) {
  def withEnoughMoney(command: Withdraw): AccountBuilder =
    copy(balance = command.amount.value + arbitraryPositiveNumber)

  def withdrawAmountReachingMaxWithdrawal(command: Withdraw): AccountBuilder =
    copy(maxWithdrawal = command.amount.value - arbitraryPositiveNumber)

  def withoutReachingMaxWithdrawal(command: Withdraw): AccountBuilder =
    copy(maxWithdrawal = command.amount.value + arbitraryPositiveNumber)

  def withInsufficientBalance(command: Withdraw): AccountBuilder =
    copy(balance = command.amount.value - arbitraryPositiveNumber)

  def withoutOverDraftAuthorized(): AccountBuilder =
    copy(isOverdraftAuthorized = false)

  def withOverDraftAuthorized(): AccountBuilder =
    copy(isOverdraftAuthorized = true)

  def build(): Account =
    Account(balance, isOverdraftAuthorized, maxWithdrawal)

  private def arbitraryPositiveNumber: Double =
    posNum[Double].sample.getOrElse(1)
}
