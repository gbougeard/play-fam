package generators

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import scala.Some
import models.TypMatch

/**
 * Created by gbougeard on 25/04/14.
 */
trait TypMatchGen {

  lazy val genTypMatch: Gen[TypMatch] = for {
    id <- arbitrary[Long]
    code <- arbitrary[String]
    name <- arbitrary[String]
    nbSubstitute <- arbitrary[Int]
    nbPlayer <- arbitrary[Int]
    periodDuration <- arbitrary[Int]
    hasExtraTime <- arbitrary[Boolean]
    extraTimeDuration <- arbitrary[Int]
    hasInfiniteSubs <- arbitrary[Boolean]
    nbSubstitution <- arbitrary[Int]
    hasPenalties <- arbitrary[Boolean]
    nbPenalties <- arbitrary[Int]
  } yield TypMatch(
      Some(id),
      code,
      name,
      nbSubstitute,
      nbPlayer,
      periodDuration,
      hasExtraTime,
      Some(extraTimeDuration),
      hasInfiniteSubs,
      Some(nbSubstitution),
      hasPenalties,
      Some(nbPenalties)
    )

  implicit lazy val arbTeam: Arbitrary[TypMatch] = Arbitrary(genTypMatch)
}
