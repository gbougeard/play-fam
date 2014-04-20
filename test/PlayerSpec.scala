import models.Player
import models.PlayerJson._


import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.mutable._
import org.specs2.ScalaCheck

import play.api.libs.json.{JsSuccess, Json}
import scala.Some

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class PlayerSpec extends Specification with ScalaCheck with PlayerGen {
  "json from(to) iso" ! prop {
    (p: Player) =>
//      println(s"p: $p")
//      println(s"toJson: ${Json.toJson(p)}")
//      println(s"from(to): ${Json.fromJson(Json.toJson(p))}")

      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }
}


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