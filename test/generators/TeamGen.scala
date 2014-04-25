package generators

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import scala.Some
import models.Team

/**
 * Created by gbougeard on 25/04/14.
 */
trait TeamGen {

  lazy val genTeam: Gen[Team] = for {
    id <- arbitrary[Long]
    code <- arbitrary[String]
    name <- arbitrary[String]
    clubId <- arbitrary[Long]
  } yield Team(
      Some(id),
      code,
      name,
      clubId
    )

  implicit lazy val arbTeam: Arbitrary[Team] = Arbitrary(genTeam)
}
