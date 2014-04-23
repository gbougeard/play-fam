import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import org.specs2.matcher.ShouldMatchers
import scala.Some


/**
 * test the kitty cat database
 */
class PlaceDBSpec extends FamSpecification with ShouldMatchers with PlaceGen {


  "PlaceDB" should {
    "insert data and retrieve them" in new WithApplication(fakeAppMemDBMinimal) {

      DB.withSession {
        implicit s: Session =>

          val testPlace = genPlace.sample.get.copy(id = Some(1))
          models.Places.insert(testPlace)
          models.Places.findAll must equalTo(Seq(testPlace))
      }
    }

  }

}
