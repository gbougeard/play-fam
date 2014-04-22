package test

import org.specs2.mutable._

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.test.Helpers._
import models._
import models.database.Answers

/**
 * test the kitty cat database
 */
class AnswerDBSpec extends Specification {

  "AnswerDB" should {
    "work as expected" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {

      //create an instance of the table
      val answers = TableQuery[Answers]

      DB.withSession {
        implicit s: Session =>
          val testAnswers = Seq(
            Answer(eventId = 1, playerId = 1, typAnswerId = 1),
            Answer(eventId = 1, playerId = 2, typAnswerId = 2, comments = Some("Absent")),
            Answer(eventId = 2, playerId = 1, typAnswerId = 1))
          answers.insertAll(testAnswers: _*)
          answers.list must equalTo(testAnswers)
      }
    }

    "select the correct testing db settings by default" in new WithApplication(FakeApplication(additionalConfiguration = inMemoryDatabase())) {
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
