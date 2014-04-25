import generators.{ClubGen, TeamGen}
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.http.MimeTypes
import play.api.libs.json.Json
import play.api.test._
import models._
import models.database._
import models.TeamJson._
import org.specs2.matcher.ShouldMatchers
import scala.Some

/**
 * test the kitty cat database
 */
class TeamDBSpec extends FamSpecification with ShouldMatchers with TeamGen with ClubGen {


  "TeamDB" should {
    "insert data and retrieve them" in new WithApplication(fakeAppMemDBMinimal) {

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

    "json - insert a team through DAO and retrieve it as json" in new WithApplication(fakeAppMemDBMinimal) {

      val club = genClub.sample.get.copy(id = Some(1))
      val team = genTeam.sample.get.copy(id = Some(1), clubId = 1)
      val teamJson = Json.toJson(team)
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
      models.Teams.insert(team)

      val getRequest = fakeGETasJson("/teams/1")
      val result = route(getRequest).get

      status(result) must equalTo(OK)
      contentType(result) must beSome.which(_ == MimeTypes.JSON)
      contentAsString(result) must beEqualTo(teamJson.toString())
    }

  }

}
