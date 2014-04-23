import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import models._
import models.database._
import org.specs2.matcher.ShouldMatchers
import scala.Some
import play.api.test.FakeApplication

/**
 * test the kitty cat database
 */
class TeamDBSpec extends PlaySpecification with ShouldMatchers {

  def app = FakeApplication(additionalConfiguration = inMemoryDatabase())

  def minimalApp = FakeApplication(additionalConfiguration = inMemoryDatabase("default"), withoutPlugins = Seq("service.SlickUserService"))

  "TeamDB" should {
    "insert data and retrieve them" in new WithApplication(app) {

      //create an instance of the table
      val clubs = TableQuery[Clubs]
      val teams = TableQuery[Teams]

      DB.withSession {
        implicit s: Session =>

          val testClubs = Seq(
            Club(Some(1), name = "club1", code = 1),
            Club(Some(2), name = "club2", code = 2),
            Club(Some(3), name = "club3", code = 3)
          )
          clubs.insertAll(testClubs: _*)

          val testTeams = Seq(
            Team(Some(1), name = "team1_1", code = "t1_1", clubId = 1),
            Team(Some(2), name = "team1_2", code = "t1_2", clubId = 1),
            Team(Some(3), name = "team2_1", code = "t2_1", clubId = 2)
          )
          teams.insertAll(testTeams: _*)
          teams.list must equalTo(testTeams)
      }
    }

  }

}
