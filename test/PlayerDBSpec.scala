import models.Club
import models.database.{Users, Teams}
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import org.specs2.matcher.ShouldMatchers
import scala.Some
import play.api.test.FakeApplication
import securesocial.core.AuthenticationMethod


/**
 * test the kitty cat database
 */
class PlayerDBSpec extends PlaySpecification with ShouldMatchers with PlayerGen {

  def app = FakeApplication(additionalConfiguration = inMemoryDatabase())

  def minimalApp = FakeApplication(additionalConfiguration = inMemoryDatabase("default"), withoutPlugins = Seq("service.SlickUserService"))

  "PlayerDB" should {
    "insert data without User associated and retrieve them" in new WithApplication(app) {

      DB.withSession {
        implicit s: Session =>

          val testPlayer = genPlayer.sample.get.copy(id = Some(1), userId = None)
          models.Players.insert(testPlayer)
          models.Players.findAll must equalTo(Seq(testPlayer))
      }
    }

    "insert data with User associated and retrieve them" in new WithApplication(app) {

      DB.withSession {
        implicit s: Session =>

          val users = TableQuery[Users]
          val testUsers = Seq(
            models.User(pid = Some(1), userId = "user1", providerId = "prov1", authMethod = AuthenticationMethod.OAuth1),
            models.User(pid = Some(2), userId = "user2", providerId = "prov2", authMethod = AuthenticationMethod.OAuth2),
            models.User(pid = Some(3), userId = "user3", providerId = "prov2", authMethod = AuthenticationMethod.UserPassword)
          )
          users.insertAll(testUsers: _*)

          val testPlayer = genPlayer.sample.get.copy(id = Some(1), userId = Some(1))
          models.Players.insert(testPlayer)
          models.Players.findAll must equalTo(Seq(testPlayer))
      }
    }

  }

}
