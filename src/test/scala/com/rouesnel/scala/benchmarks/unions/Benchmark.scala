package com.rouesnel.scala.benchmarks.unions

import org.scalameter.api._

import scalaz._, syntax.either._

import shapeless._

sealed trait StringOrIntOrBool
case class IntValue(s: Int) extends StringOrIntOrBool
case class StringValue(s: String) extends StringOrIntOrBool
case class BoolValue(s: Boolean) extends StringOrIntOrBool

object Benchmark extends PerformanceTest.OfflineReport {
  val sizes: Gen[Int] = Gen.exponential("iterations")(1, 1000000, 10)
  val ranges: Gen[Range] = sizes.map(upper ⇒ 0 until upper)

  def patternMatch(a: Any): Int = a match {
    case int: Int       ⇒ int + 1
    case string: String ⇒ string.toInt + 1
    case bool: Boolean  ⇒ if (bool) 1 else 0
  }

  def adt(a: StringOrIntOrBool): Int = a match {
    case IntValue(int)       ⇒ int + 1
    case StringValue(string) ⇒ string.toInt + 1
    case BoolValue(bool)     ⇒ if (bool) 1 else 0
  }

  def scalazEither(a: Int \/ String \/ Boolean): Int =
    a.fold(_.fold(_ + 1, _.toInt + 1), b ⇒ if (b) 1 else 0)

  type IntStringBool = Int :+: String :+: Boolean :+: CNil
  def shapelessCoproduct(a: IntStringBool): Int = {
    object extract extends Poly1 {
      implicit def caseInt = at[Int](int ⇒ int + 1)
      implicit def caseString = at[String](string ⇒ string.toInt + 1)
      implicit def caseBoolean = at[Boolean](bool ⇒ if (bool) 1 else 0)
    }
    a.map(extract).select[Int].get
  }

  def shapelessCoproductSelect(a: IntStringBool): Int = {
    a.select[Int].map(_ + 1).orElse(
      a.select[String].map(_.toInt + 1)).orElse(
        a.select[Boolean].map(bool ⇒ if (bool) 1 else 0)).get
  }

  performance of "Union Types" in {
    performance of "pattern matching" in {
      using(ranges) in { data ⇒
        data.foreach(i ⇒ {
          i % 4 match {
            case 0 ⇒ patternMatch(i)
            case 1 ⇒ patternMatch(i.toString)
            case 2 ⇒ patternMatch(true)
            case 3 ⇒ patternMatch(false)
          }
        })
      }
    }

    performance of "adt" in {
      using(ranges) in { data ⇒
        data.foreach(i ⇒ {
          i % 4 match {
            case 0 ⇒ adt(IntValue(i))
            case 1 ⇒ adt(StringValue(i.toString))
            case 2 ⇒ adt(BoolValue(true))
            case 3 ⇒ adt(BoolValue(false))
          }
        })
      }
    }

    performance of "scalaz.Either" in {
      using(ranges) in { data ⇒
        data.foreach(i ⇒ {
          i % 4 match {
            case 0 ⇒ scalazEither(i.left.left)
            case 1 ⇒ scalazEither(i.toString.right.left)
            case 2 ⇒ scalazEither(true.right)
            case 3 ⇒ scalazEither(false.right)
          }
        })
      }
    }

    performance of "shapeless.Coproduct (map select)" in {
      using(ranges) in { data ⇒
        data.foreach(i ⇒ {
          i % 4 match {
            case 0 ⇒ shapelessCoproduct(shapeless.Coproduct[IntStringBool](i))
            case 1 ⇒ shapelessCoproduct(shapeless.Coproduct[IntStringBool](i.toString))
            case 2 ⇒ shapelessCoproduct(shapeless.Coproduct[IntStringBool](true))
            case 3 ⇒ shapelessCoproduct(shapeless.Coproduct[IntStringBool](false))
          }
        })
      }
    }

    performance of "shapeless.Coproduct (select)" in {
      using(ranges) in { data ⇒
        data.foreach(i ⇒ {
          i % 4 match {
            case 0 ⇒ shapelessCoproductSelect(shapeless.Coproduct[IntStringBool](i))
            case 1 ⇒ shapelessCoproductSelect(shapeless.Coproduct[IntStringBool](i.toString))
            case 2 ⇒ shapelessCoproductSelect(shapeless.Coproduct[IntStringBool](true))
            case 3 ⇒ shapelessCoproductSelect(shapeless.Coproduct[IntStringBool](false))
          }
        })
      }
    }
  }
}
