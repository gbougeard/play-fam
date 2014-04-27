import generators.TypMatchGen
import models.database.TypMatches
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.test._
import models._
import models.TypMatchJson._
import org.specs2.matcher.ShouldMatchers
import scala.Some


/**
 * test the kitty cat database
 */
class TypMatchDBSpec extends FamSpecification with ShouldMatchers with TypMatchGen  {


  "TypMatchDB" should {
    "insert data and retrieve them" in new WithApplication(fakeAppMemDBMinimal) {

      //create an instance of the table
      val typMatchs = TableQuery[TypMatches]

      DB.withSession {
        implicit s: Session =>

          val testTypMatchs = Seq(
            genTypMatch.sample.get.copy(id = Some(1)),
            genTypMatch.sample.get.copy(id = Some(2)),
            genTypMatch.sample.get.copy(id = Some(3))
          )
          typMatchs.insertAll(testTypMatchs: _*)
          typMatchs.list must equalTo(testTypMatchs)
      }
    }

    "json - insert a typMatch through DAO and retrieve it as json" in new WithApplication(fakeAppMemDBMinimal) {

      val typMatch = genTypMatch.sample.get.copy(id = Some(1))
      val typMatchJson = Json.toJson(typMatch)
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
      models.TypMatches.insert(typMatch)

      val getRequest = fakeGETasJson("/typMatches/1")
      val result = route(getRequest).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == MimeTypes.JSON)
      contentAsString(result) must beEqualTo(typMatchJson.toString())
    }

  }

}
