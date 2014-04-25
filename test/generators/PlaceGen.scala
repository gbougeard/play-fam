package generators

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import scala.Some
import models.Place

/**
 * Created by gbougeard on 25/04/14.
 */
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
