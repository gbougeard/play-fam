package generators

import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import models.Club
import scala.Some

/**
 * Created by gbougeard on 25/04/14.
 */
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
