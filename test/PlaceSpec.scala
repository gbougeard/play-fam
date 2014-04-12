import models.Place
import json.ImplicitGlobals._

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

class PlaceSpec extends Specification with ScalaCheck with PlaceGen {

  "json from(to) iso" ! prop {
    (p: Place) =>
    //      println(s"p: $p")
    //      println(s"toJson: ${Json.toJson(p)}")
    //      println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }
}


trait PlaceGen {

  lazy val genPlace: Gen[Place] = for {
    id <- arbitrary[Long]
    name <- arbitrary[String]
    address <- arbitrary[String]
    city <- arbitrary[String]
    zipcode <- arbitrary[String]
    latitude <- arbitrary[Float]
    longitude <- arbitrary[Float]
    comments <- arbitrary[String]
    typFff <- arbitrary[String]
  } yield Place(
      Some(id),
      name,
      address,
      city,
      zipcode,
      Some(latitude),
      Some(longitude),
      Some(comments),
      Some(typFff))

  implicit lazy val arbPlace: Arbitrary[Place] = Arbitrary(genPlace)
}

