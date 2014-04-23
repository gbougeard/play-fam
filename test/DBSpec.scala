import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import org.specs2.matcher.ShouldMatchers


/**
 * test the kitty cat database
 */
class DBSpec extends FamSpecification with ShouldMatchers {

  "DB" should {

    "select the correct testing db settings by default" in new WithApplication(fakeAppMemDBMinimal) {
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
