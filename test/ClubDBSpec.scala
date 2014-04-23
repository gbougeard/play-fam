import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.test.Helpers._
import models._
import models.database._
import org.specs2.matcher.ShouldMatchers
import scala.Some
import play.api.test.FakeApplication

/**
 * test the kitty cat database
 */
class ClubDBSpec extends FamSpecification with ShouldMatchers {


  "ClubDB" should {
    "insert data and retrieve them" in new WithApplication(fakeAppMemDBMinimal) {

      //create an instance of the table
      val clubs = TableQuery[Clubs]

      DB.withSession {
        implicit s: Session =>

          val testClubs = Seq(
            Club(Some(1), name = "club1", code = 1),
            Club(Some(2), name = "club2", code = 2),
            Club(Some(3), name = "club3", code = 3)
          )
          clubs.insertAll(testClubs: _*)
          clubs.list must equalTo(testClubs)
      }
    }

  }

}
