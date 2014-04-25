import generators.PlaceGen
import models.Place
import models.PlaceJson._

import org.specs2.ScalaCheck

import play.api.http.MimeTypes
import play.api.libs.json.{JsSuccess, Json}
import play.api.test.WithApplication
import scala.Some

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class PlaceSpec extends FamSpecification with ScalaCheck with PlaceGen {

  "json from(to) iso" ! prop {
    (p: Place) =>
    //      println(s"p: $p")
    //      println(s"toJson: ${Json.toJson(p)}")
    //      println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }

  "json - insert a place through DAO and retrieve it as json" in new WithApplication(fakeAppMemDBMinimal) {

    val place = genPlace.sample.get.copy(id = Some(1))
    //    println(s"club $club")
    val placeJson = Json.toJson(place)
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
    models.Places.insert(place)

    val getRequest = fakeGETasJson("/places/1")
    val result = route(getRequest).get

    status(result) must equalTo(OK)
    contentType(result) must beSome.which(_ == MimeTypes.JSON)
    contentAsString(result) must beEqualTo(placeJson.toString())
  }
}




