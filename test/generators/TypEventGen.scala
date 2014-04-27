package generators

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import scala.Some
import models.TypEvent

/**
 * Created by gbougeard on 25/04/14.
 */
trait TypEventGen {

  lazy val genTypEvent: Gen[TypEvent] = for {
    id <- arbitrary[Long]
    code <- arbitrary[String]
    name <- arbitrary[String]
  } yield TypEvent(
      Some(id),
      code,
      name
    )

  implicit lazy val arbTeam: Arbitrary[TypEvent] = Arbitrary(genTypEvent)
}
