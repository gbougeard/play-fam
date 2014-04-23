import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.test.Helpers._
import models._
import models.database._
import org.specs2.matcher.ShouldMatchers
import scala.Some
import play.api.test.FakeApplication

//import securesocial.testkit.WithLoggedUser

/**
 * test the kitty cat database
 */
class ClubDBSpec extends PlaySpecification with ShouldMatchers {
  //  import WithLoggedUser._

  def app = FakeApplication(additionalConfiguration = inMemoryDatabase())

  def minimalApp = FakeApplication(additionalConfiguration = inMemoryDatabase("default"), withoutPlugins = Seq("service.SlickUserService"))

  "ClubDB" should {
    "insert data and retrieve them" in new WithApplication(app) {

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

    "select the correct testing db settings by default" in new WithApplication(minimalApp) {
      DB.withSession {
        implicit s: Session =>
          s.conn.getMetaData.getURL must startWith("jdbc:h2:mem:play-test")
      }
    }

    //    "use the default db settings when no other possible options are available" in new WithApplication {
    //      DB.withSession { implicit s: Session =>
    //        s.conn.getMetaData.getURL must equalTo("jdbc:h2:mem:play")
    //      }
    //    }
  }

}
