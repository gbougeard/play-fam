package generators

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import models.Answer
import scala.Some

/**
 * Created by gbougeard on 25/04/14.
 */
trait AnswerGen {

  lazy val genAnswer: Gen[Answer] = for {
    id <- arbitrary[Long]
    eventId <- arbitrary[Long]
    playerId <- arbitrary[Long]
    typAnswerId <- arbitrary[Long]
    comments <- arbitrary[String]
  } yield Answer(
      Some(id),
      eventId,
      playerId,
      typAnswerId,
      Some(comments)
    )

  implicit lazy val arbAnswer: Arbitrary[Answer] = Arbitrary(genAnswer)

}
