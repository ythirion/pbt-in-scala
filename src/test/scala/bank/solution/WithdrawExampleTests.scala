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

  "withdraw" should "pass examples" in {
    forAll(`withdraw examples`) {
      (
          balance,
          isOverdraftAuthorized,
          maxWithdrawal,
          withdrawAmount,
          expectedResult
      ) =>
        val command = toWithdraw(withdrawAmount)
        val debitedAccount =
          AccountService.withdraw(
            Account(balance, isOverdraftAuthorized, maxWithdrawal),
            command
          )

        expectedResult match {
          case Left(expectedErrorMessage) =>
            debitedAccount.left.value.startsWith(expectedErrorMessage)
          case Right(expectedBalance) =>
            debitedAccount.value.balance == expectedBalance &&
              debitedAccount.value.withdraws.contains(command) &&
              debitedAccount.value.withdraws.length == 1
        }
    }
  }
}

object WithdrawExampleTests {
  private val `withdraw examples` =
    Table(
      (
        "balance",
        "isOverdraftAuthorized",
        "maxWithdrawal",
        "withdrawAmount",
        "expectedResult"
      ),
      (10_000, false, 1200, 1199.99, Right(8800.01)),
      (0, true, 500, 50d, Right(-50)),
      (1, false, 1200, 1199.99, Left("Insufficient balance to withdraw")),
      (0, false, 500, 50d, Left("Insufficient balance to withdraw")),
      (10_000, true, 1200, 1200.0001, Left("Insufficient balance to withdraw")),
      (0, false, 500, 500d, Left("Insufficient balance to withdraw"))
    )

  private def toWithdraw(amount: Double): Withdraw = {
    Withdraw(
      UUID.randomUUID(),
      Amount.from(amount).get,
      LocalDate.now()
    )
  }
}
