import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import org.specs2.matcher.ShouldMatchers
import play.api.test.FakeApplication

//import securesocial.testkit.WithLoggedUser

/**
 * test the kitty cat database
 */
class DBSpec extends PlaySpecification with ShouldMatchers {
  //  import WithLoggedUser._

  def app = FakeApplication(additionalConfiguration = inMemoryDatabase())

  def minimalApp = FakeApplication(additionalConfiguration = inMemoryDatabase("default"), withoutPlugins = Seq("service.SlickUserService"))

  "DB" should {

    "select the correct testing db settings by default" in new WithApplication(minimalApp) {
      DB.withSession {
        implicit s: Session =>
          s.conn.getMetaData.getURL must startWith("jdbc:h2:mem:play-test")
      }
    }

//    "use the default db settings when no other possible options are available" in new WithApplication {
//      DB.withSession {
//        implicit s: Session =>
//          s.conn.getMetaData.getURL must equalTo("jdbc:h2:mem:play")
//      }
//    }
  }

}
