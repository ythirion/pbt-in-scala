package bank.solution

import bank.{Account, AccountService, Amount, Withdraw}
import org.scalacheck.Prop.{forAll, propBoolean}
import org.scalacheck.{Arbitrary, Gen, Properties}
import org.scalatest.EitherValues

import java.time.LocalDate
import java.util.UUID

object AccountProperties extends Properties("Account") with EitherValues {
  private val positiveDouble = Gen.choose(0.01, 1_000_000)

  implicit val accountGenerator: Arbitrary[Account] = Arbitrary {
    for {
      balance <- positiveDouble
      isOverdraftAuthorized <- Arbitrary.arbitrary[Boolean]
      maxWithdrawal <- positiveDouble
    } yield Account(balance, isOverdraftAuthorized, maxWithdrawal)
  }

  implicit val withdrawCommandGenerator: Arbitrary[Withdraw] = Arbitrary {
    for {
      clientId <- Arbitrary.arbitrary[UUID]
      amount <- positiveDouble
      requestDate <- Arbitrary.arbitrary[LocalDate]
    } yield Withdraw(clientId, Amount.from(amount).get, requestDate)
  }

  property("balance should be decremented at least from the withdraw amount") =
    forAll { (account: Account, command: Withdraw) =>
      {
        (withEnoughMoney(account, command) &&
        withoutReachingMaxWithdrawal(account, command)) ==> {
          val debitedAccount = AccountService.withdraw(account, command).value
          debitedAccount.balance <= account.balance - command.amount.value
        }
      }
    }

  private def withEnoughMoney(account: Account, command: Withdraw): Boolean =
    account.balance > command.amount.value

  private def withoutReachingMaxWithdrawal(
      account: Account,
      command: Withdraw
  ): Boolean =
    account.maxWithdrawal > command.amount.value

}
