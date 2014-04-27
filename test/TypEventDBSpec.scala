import generators.TypEventGen
import models.database.TypEvents
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.test._
import models._
import models.TypEventJson._
import org.specs2.matcher.ShouldMatchers
import scala.Some


/**
  * test the kitty cat database
  */
class TypEventDBSpec extends FamSpecification with ShouldMatchers with TypEventGen  {


   "TypEventDB" should {
     "insert data and retrieve them" in new WithApplication(fakeAppMemDBMinimal) {

       //create an instance of the table
       val typEvents = TableQuery[TypEvents]

       DB.withSession {
         implicit s: Session =>

           val testTypEvents = Seq(
             genTypEvent.sample.get.copy(id = Some(1)),
             genTypEvent.sample.get.copy(id = Some(2)),
             genTypEvent.sample.get.copy(id = Some(3))
           )
           typEvents.insertAll(testTypEvents: _*)
           typEvents.list must equalTo(testTypEvents)
       }
     }

     "json - insert a typEvent through DAO and retrieve it as json" in new WithApplication(fakeAppMemDBMinimal) {

       val typEvent = genTypEvent.sample.get.copy(id = Some(1))
       val typEventJson = Json.toJson(typEvent)
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
       models.TypEvents.insert(typEvent)

       val getRequest = fakeGETasJson("/typEvents/1")
       val result = route(getRequest).get

       status(result) must equalTo(OK)
       contentType(result) must beSome.which(_ == MimeTypes.JSON)
       contentAsString(result) must beEqualTo(typEventJson.toString())
     }

   }

 }
