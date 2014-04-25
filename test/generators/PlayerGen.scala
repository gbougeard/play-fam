package generators

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import models.Player
import scala.Some

/**
 * Created by gbougeard on 25/04/14.
 */
trait PlayerGen {

  lazy val genPlayer: Gen[Player] = for {
    id <- arbitrary[Long]
    firstname <- arbitrary[String]
    lastname <- arbitrary[String]
    email <- arbitrary[String]
    userid <- arbitrary[Long]
  } yield Player(
      Some(id),
      firstname,
      lastname,
      email,
      Some(userid))

  implicit lazy val arbPlayer: Arbitrary[Player] = Arbitrary(genPlayer)
}
