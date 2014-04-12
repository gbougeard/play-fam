import models.Club
import json.ImplicitGlobals._

import play.api.libs.json._

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.mutable._
import org.specs2.ScalaCheck



/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class ClubSpec extends Specification with ScalaCheck with ClubGen {

  "json from(to) iso" ! prop {
    (p: Club) =>
//          println(s"p: $p")
//          println(s"toJson: ${Json.toJson(p)}")
//          println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }
}


trait ClubGen {

  lazy val genClub: Gen[Club] = for {
    id <- arbitrary[Long]
    code <- arbitrary[Int]
    name <- arbitrary[String]
    countryId <- arbitrary[Long]
    cityId <- arbitrary[Long]
    colours <- arbitrary[String]
    address <- arbitrary[String]
    zipcode <- arbitrary[String]
    city <- arbitrary[String]
    orgaId <- arbitrary[Long]
    comments <- arbitrary[String]
  } yield Club(
      Some(id),
      code,
      name,
      Some(countryId),
      Some(cityId),
      Some(colours),
      Some(address),
      Some(zipcode),
      Some(city),
      Some(orgaId),
      Some(comments)
    )

  implicit lazy val arbClub: Arbitrary[Club] = Arbitrary(genClub)
}
