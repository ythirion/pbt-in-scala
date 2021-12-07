package bank.solution

import bank.solution.WithdrawProperties._
import bank.{Account, AccountService, Amount, Withdraw}
import org.scalacheck.Arbitrary
import org.scalacheck.Gen.posNum
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.Checkers

import java.time.LocalDate
import java.util.UUID

class WithdrawProperties extends AnyFlatSpec with Checkers with EitherValues {
  behavior of "Withdraw"

  "account balance" should "be decremented at least from the withdraw amount" in {
    checkProperty(
      (account, command) => {
        withEnoughMoney(account, command) &&
          withoutReachingMaxWithdrawal(account, command)
      },
      (account, command, debitedAccount) =>
        debitedAccount.value.balance <= account.balance - command.amount.value
    )
  }

  "account balance" should "be decremented at least from the withdraw amount when insufficient balance but overdraft authorized" in {
    checkProperty(
      (account, command) => {
        withOverdraftAuthorized(account) &&
          withInsufficientBalance(account, command) &&
          withoutReachingMaxWithdrawal(account, command)
      },
      (account, command, debitedAccount) =>
        debitedAccount.value.balance <= account.balance - command.amount.value
    )
  }

  "withdraw" should "not be allowed when withdraw amount >= maxWithdrawal" in {
    checkProperty(
      (account, command) => reachingMaxWithdrawal(account, command),
      (_, _, debitedAccount) =>
        debitedAccount.left.value
          .startsWith("Amount exceeding your limit")
    )
  }

  "withdraw" should "not be allowed when insufficient balance and no overdraft" in {
    checkProperty(
      (account, command) => {
        withInsufficientBalance(account, command) &&
          withoutOverdraftAuthorized(account) &&
          withoutReachingMaxWithdrawal(account, command)
      },
      (_, _, debitedAccount) => {
        debitedAccount.left.value
          .startsWith("Insufficient balance to withdraw")
      }
    )
  }

  implicit override val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSize = 10, maxDiscardedFactor = 30)

  implicit val accountGenerator: Arbitrary[Account] = Arbitrary {
    for {
      balance               <- Arbitrary.arbitrary[Double]
      isOverdraftAuthorized <- Arbitrary.arbitrary[Boolean]
      maxWithdrawal         <- Arbitrary.arbitrary[Double]
    } yield Account(balance, isOverdraftAuthorized, maxWithdrawal)
  }

  implicit val withdrawCommandGenerator: Arbitrary[Withdraw] = Arbitrary {
    for {
      clientId    <- Arbitrary.arbitrary[UUID]
      amount      <- posNum[Double]
      requestDate <- Arbitrary.arbitrary[LocalDate]
    } yield Withdraw(clientId, Amount.from(amount).get, requestDate)
  }

  private def checkProperty(
      when: (Account, Withdraw) => Boolean,
      property: (Account, Withdraw, Either[String, Account]) => Boolean
  ): Assertion = {
    check(forAll { (account: Account, command: Withdraw) =>
      when(account, command) ==> {
        property(account, command, AccountService.withdraw(account, command))
      }
    })
  }
}

object WithdrawProperties {
  private def withEnoughMoney(account: Account, command: Withdraw): Boolean =
    account.balance > command.amount.value

  private def withInsufficientBalance(
      account: Account,
      command: Withdraw
  ): Boolean =
    account.balance < command.amount.value

  private def withOverdraftAuthorized(account: Account): Boolean =
    account.isOverdraftAuthorized

  private def withoutOverdraftAuthorized(account: Account): Boolean =
    !account.isOverdraftAuthorized

  private def withoutReachingMaxWithdrawal(
      account: Account,
      command: Withdraw
  ): Boolean =
    account.maxWithdrawal > command.amount.value

  private def reachingMaxWithdrawal(
      account: Account,
      command: Withdraw
  ): Boolean =
    command.amount.value >= account.maxWithdrawal
}
