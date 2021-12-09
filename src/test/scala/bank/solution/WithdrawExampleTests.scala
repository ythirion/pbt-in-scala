package bank.solution

import bank.solution.WithdrawExampleTests._
import bank.{Account, AccountService, Amount, Withdraw}
import org.scalatest.EitherValues
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.prop.TableDrivenPropertyChecks
import org.scalatest.prop.Tables.Table
import org.scalatestplus.scalacheck.Checkers

import java.time.LocalDate
import java.util.UUID

class WithdrawExampleTests
    extends AnyFlatSpec
    with TableDrivenPropertyChecks
    with Checkers
    with EitherValues {

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
          assertDebitedAccount(
            Account(balance, isOverdraftAuthorized, maxWithdrawal),
            withdrawAmount,
            (withdraw, debitedAccount) => {
              debitedAccount.value.balance == expectedBalance &&
                debitedAccount.value.withdraws.contains(withdraw) &&
                debitedAccount.value.withdraws.length == 1
            }
          )
        }
    }
  }

  "withdraw" should "not be authorized when insufficient balance and no overdraft" in {
    forAll(`account with insufficient balance and no overdraft`) {
      (
          balance,
          maxWithdrawal,
          withdrawAmount
      ) =>
        {
          assertDebitedAccount(
            Account(balance, isOverdraftAuthorized = false, maxWithdrawal),
            withdrawAmount,
            (_, debitedAccount) => {
              debitedAccount.left.value
                .startsWith("Insufficient balance to withdraw")
            }
          )
        }
    }
  }

  "withdraw" should "not be authorized when withdraw amount > account maxWithdrawal" in {
    forAll(`withdraw amount > account maxWithdrawal`) {
      (
          balance,
          maxWithdrawal,
          withdrawAmount
      ) =>
        {
          assertDebitedAccount(
            Account(balance, isOverdraftAuthorized = false, maxWithdrawal),
            withdrawAmount,
            (_, debitedAccount) => {
              debitedAccount.left.value
                .startsWith("Amount exceeding your limit of")
            }
          )
        }
    }
  }
}

object WithdrawExampleTests {
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

  private val `withdraw amount > account maxWithdrawal` =
    Table(
      (
        "balance",
        "maxWithdrawal",
        "withdrawAmount"
      ),
      (10_000, 1200, 1200.0001),
      (0, 500, 500d)
    )

  private def toWithdraw(withdrawAmount: Double) = {
    Withdraw(
      UUID.randomUUID(),
      Amount.from(withdrawAmount).get,
      LocalDate.now()
    )
  }

  private def assertDebitedAccount(
      account: Account,
      withdrawAmount: Double,
      assertion: (Withdraw, Either[String, Account]) => Boolean
  ): Unit = {
    val withdrawCommand = toWithdraw(withdrawAmount)
    val debitedAccount =
      AccountService
        .withdraw(
          account,
          withdrawCommand
        )

    assert(assertion(withdrawCommand, debitedAccount))
  }
}
