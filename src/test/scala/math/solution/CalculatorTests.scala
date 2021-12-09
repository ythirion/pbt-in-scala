package math.solution

import math.Calculator.add
import math.solution.CalculatorTests.{random, times}
import org.scalatest.Assertion
import org.scalatest.flatspec.AnyFlatSpec

import scala.util.Random

class CalculatorTests extends AnyFlatSpec {
  behavior of "Calculator"

  it should "return 4 when I add 1 to 3" in {
    assert(add(1, 3) == 4)
  }

  it should "return 2 when I add -1 to 3" in {
    assert(add(-1, 3) == 2)
  }

  it should "return 99 when I add 0 to 99" in {
    assert(add(99, 0) == 99)
  }

  it should "return their correct sum when I add 2 random numbers" in {
    Range(0, 100)
      .foreach(_ => {
        val x = random.nextInt()
        val y = random.nextInt()

        assert(add(x, y) == x + y)
      })
  }

  // Express addition properties in tests
  it should "when I add 2 numbers the result should not depend on parameter order" in {
    Range(0, times)
      .map(_ => random.nextInt())
      .foreach(x => {
        val y = random.nextInt()
        assert(add(x, y) == add(y, x))
      })
  }

  it should "when I add 1 twice it's the same as adding 2 once" in {
    Range(0, times)
      .map(_ => random.nextInt())
      .foreach(x => assert(add(add(x, 1), 1) == add(x, 2)))
  }

  it should "when I add 0 to a random number is the same than doing nothing on this number" in {
    Range(0, times)
      .map(_ => random.nextInt())
      .foreach(x => assert(add(x, 0) == x))
  }
}

object CalculatorTests {
  val random: Random = Random
  val times          = 100

  // We should use the runProperty function to avoid duplication in our tests
  def runProperty(times: Int, f: Int => Assertion): Unit = {
    val random = Random

    Range(0, times)
      .map(_ => random.nextInt())
      .foreach(f)
  }
}
