package bank.solution

import bank.solution.WithdrawPropertiesWithAccountBuilder.checkProperty
import bank.{Account, AccountService, Amount, Withdraw}
import org.scalacheck.Arbitrary
import org.scalacheck.Gen.posNum
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.{Assertion, EitherValues}
import org.scalatestplus.scalacheck.Checkers
import org.scalatestplus.scalacheck.Checkers.check

import java.time.LocalDate
import java.util.UUID

class WithdrawPropertiesWithAccountBuilder
    extends AnyFlatSpec
    with Checkers
    with EitherValues {

  "withdraw" should "be idempotent" in {
    checkProperty(
      (account, command) =>
        account
          .withEnoughMoney(command)
          .withoutReachingMaxWithdrawal(command),
      (_, command, debitedAccount) =>
        AccountService.withdraw(debitedAccount.value, command) == debitedAccount
    )
  }

  "account balance" should "be decremented at least from the withdraw amount" in {
    checkProperty(
      (account, command) =>
        account
          .withEnoughMoney(command)
          .withoutReachingMaxWithdrawal(command),
      (account, command, debitedAccount) =>
        debitedAccount.value.balance <= account.balance - command.amount.value
    )
  }

  "account balance" should "be decremented at least from the withdraw amount when insufficient balance but overdraft authorized" in {
    checkProperty(
      (account, command) =>
        account
          .withInsufficientBalance(command)
          .withoutReachingMaxWithdrawal(command)
          .withOverDraftAuthorized(),
      (account, command, debitedAccount) =>
        debitedAccount.value.balance <= account.balance - command.amount.value
    )
  }

  "withdraw" should "not be allowed when withdraw amount >= maxWithdrawal" in {
    checkProperty(
      (
          account,
          command
      ) => account.withdrawAmountReachingMaxWithdrawal(command),
      (_, _, debitedAccount) =>
        debitedAccount.left.value
          .startsWith("Amount exceeding your limit")
    )
  }

  "withdraw" should "not be allowed when insufficient balance and no overdraft" in {
    checkProperty(
      (account, command) =>
        account
          .withInsufficientBalance(command)
          .withoutOverDraftAuthorized()
          .withoutReachingMaxWithdrawal(command),
      (_, _, debitedAccount) =>
        debitedAccount.left.value
          .startsWith("Insufficient balance to withdraw")
    )
  }
}

object WithdrawPropertiesWithAccountBuilder {
  implicit val withdrawCommandGenerator: Arbitrary[Withdraw] = Arbitrary {
    for {
      clientId    <- Arbitrary.arbitrary[UUID]
      amount      <- posNum[Double]
      requestDate <- Arbitrary.arbitrary[LocalDate]
    } yield Withdraw(clientId, Amount.from(amount).get, requestDate)
  }

  private def checkProperty(
      accountConfiguration: (AccountBuilder, Withdraw) => AccountBuilder,
      property: (Account, Withdraw, Either[String, Account]) => Boolean
  ): Assertion = {
    check(forAll { (command: Withdraw) =>
      val account = accountConfiguration(AccountBuilder(), command).build()
      property(account, command, AccountService.withdraw(account, command))
    })
  }
}
