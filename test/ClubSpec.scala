import generators.ClubGen
import models.Club
import models.ClubJson._

import play.api.http.MimeTypes
import play.api.libs.json._

import org.specs2.ScalaCheck

import play.api.test.WithApplication
import scala.Some


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

    val getRequest = fakeGETasJson("/clubs/1")
    val clubs = route(getRequest).get

    status(clubs) must equalTo(OK)
    contentType(clubs) must beSome.which(_ == MimeTypes.JSON)
    contentAsString(clubs) must beEqualTo(clubJson.toString())
  }
}



