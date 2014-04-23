import models.Club
import models.ClubJson._

import play.api.libs.json._

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck
import play.api.mvc.AnyContentAsEmpty
import play.api.test.{FakeHeaders, FakeRequest, WithApplication}


/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class ClubSpec extends FamSpecification with ScalaCheck with ClubGen {

  "json - from(to) iso" ! prop {
    (p: Club) =>
    //          println(s"p: $p")
    //          println(s"toJson: ${Json.toJson(p)}")
    //          println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }

  "json - insert a club through DAO and retrieve it as json" in new WithApplication(fakeAppMemDBMinimal) {

    val club = genClub.sample.get.copy(id = Some(1))
//    println(s"club $club")
    val clubJson = Json.toJson(club)
//    val postRequest = FakeRequest(
//      method = "POST",
//      uri = "/clubs",
//      headers = FakeHeaders(
//        Seq("Content-type" -> Seq("application/json"))
//      ),
//      body = clubJson
//    )
//    val Some(result) = route(postRequest)
//    status(result) must equalTo(OK)
    models.Clubs.insert(club)
    
    val jsonHeaders = FakeHeaders(Seq("Accept" -> Seq("application/json")))
    val getRequest = FakeRequest(method = "GET", uri = "/clubs/1", headers = jsonHeaders, body = AnyContentAsEmpty)

    val clubs = route(getRequest).get

    status(clubs) must equalTo(OK)
    contentType(clubs) must beSome.which(_ == "application/json")
    contentAsString(clubs) must beEqualTo(clubJson.toString())
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
