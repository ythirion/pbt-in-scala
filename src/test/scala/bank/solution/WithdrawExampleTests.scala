package bank.solution

import bank.{Account, AccountService, Amount, Withdraw}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatestplus.scalacheck.Checkers

import java.time.LocalDate
import java.util.UUID

class WithdrawExampleTests
    extends AnyFlatSpec
    with TableDrivenPropertyChecks
    with Checkers
    with EitherValues {
  private val `account authorized to withdraw` =
    Table(
      (
        "balance",
        "isOverdraftAuthorized",
        "maxWithdrawal",
        "withdrawAmount",
        "expectedBalance"
      ),
      (10_000, false, 1200, 1199.99, 8800.01),
      (0, true, 500, 50d, -50)
    )

  "withdraw on account with enough money or overdraft authorized" should "match expected balance" in {
    forAll(`account authorized to withdraw`) {
      (
          balance,
          isOverdraftAuthorized,
          maxWithdrawal,
          withdrawAmount,
          expectedBalance
      ) =>
        {
          val withdrawCommand = Withdraw(
            UUID.randomUUID(),
            Amount.from(withdrawAmount).get,
            LocalDate.now()
          )

          val debitedAccount =
            AccountService
              .withdraw(
                Account(balance, isOverdraftAuthorized, maxWithdrawal),
                withdrawCommand
              )
              .value

          assert(debitedAccount.balance == expectedBalance)
          assert(debitedAccount.withdraws.contains(withdrawCommand))
          assert(debitedAccount.withdraws.length == 1)
        }
    }
  }

  private val `account with insufficient balance and no overdraft` =
    Table(
      (
        "balance",
        "maxWithdrawal",
        "withdrawAmount"
      ),
      (1, 1200, 1199.99),
      (0, 500, 50d)
    )

  "withdraw" should "not be authorized when insufficient balance and no overdraft" in {
    forAll(`account with insufficient balance and no overdraft`) {
      (
          balance,
          maxWithdrawal,
          withdrawAmount
      ) =>
        {
          val withdrawCommand = Withdraw(
            UUID.randomUUID(),
            Amount.from(withdrawAmount).get,
            LocalDate.now()
          )

          assert(
            AccountService
              .withdraw(
                Account(balance, isOverdraftAuthorized = false, maxWithdrawal),
                withdrawCommand
              )
              .left
              .value
              .startsWith("Insufficient balance to withdraw")
          )
        }
    }
  }

  private val `withdraw amount > account maxWithdrawal` =
    Table(
      (
        "balance",
        "isOverdraftAuthorized",
        "maxWithdrawal",
        "withdrawAmount"
      ),
      (10_000, false, 1200, 1200.0001),
      (0, true, 500, 500d)
    )

  "withdraw" should "not be authorized when withdraw amount > account maxWithdrawal" in {
    forAll(`withdraw amount > account maxWithdrawal`) {
      (
          balance,
          isOverdraftAuthorized,
          maxWithdrawal,
          withdrawAmount
      ) =>
        {
          val withdrawCommand = Withdraw(
            UUID.randomUUID(),
            Amount.from(withdrawAmount).get,
            LocalDate.now()
          )

          assert(
            AccountService
              .withdraw(
                Account(balance, isOverdraftAuthorized, maxWithdrawal),
                withdrawCommand
              )
              .left
              .value
              .startsWith("Amount exceeding your limit of")
          )
        }
    }
  }
}
