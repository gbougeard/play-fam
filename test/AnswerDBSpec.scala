package test

import org.specs2.mutable._

import play.api.test.PlaySpecification
import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import play.api.test._
import play.api.test.Helpers._
import models._
import models.database._
import org.specs2.matcher.ShouldMatchers
import org.joda.time.DateTime
import models.database.Players
import models.Answer
import scala.Some
import models.TypAnswer
import models.Player
import models.database.TypAnswers
import models.database.Answers
import models.Event
import models.database.Events
import play.api.test.FakeApplication

/**
 * test the kitty cat database
 */
class AnswerDBSpec extends PlaySpecification with ShouldMatchers {

  def app = FakeApplication(additionalConfiguration = inMemoryDatabase())

  def minimalApp = FakeApplication(additionalConfiguration = inMemoryDatabase("default"), withoutPlugins = Seq("service.SlickUserService"))

  "AnswerDB" should {
    "insert data and retrieve them" in new WithApplication(app) {

      //create an instance of the table
      val answers = TableQuery[Answers]
      val events = TableQuery[Events]
      val players = TableQuery[Players]
      val typAnswers = TableQuery[TypAnswers]
      val typEvents = TableQuery[TypEvents]
      val eventStatuses = TableQuery[EventStatuses]

      DB.withSession {
        implicit s: Session =>

          val testTypEvents = Seq(
            TypEvent(code = "code1", name = "nom1"),
            TypEvent(code = "code2", name = "nom2")
          )
          typEvents.insertAll(testTypEvents: _*)

          val testEventStatuses = Seq(
            EventStatus(code = "code1", name = "nom1"),
            EventStatus(code = "code2", name = "nom2")
          )
          eventStatuses.insertAll(testEventStatuses: _*)


          val now = new DateTime()
          val tomorrow = now.plusDays(1)
          val testEvents = Seq(
            Event(dtEvent = now, name = "nom1", duration = 60, typEventId = 1L, eventStatusId = 1L),
            Event(dtEvent = tomorrow, name = "nom2", duration = 90, typEventId = 1L, eventStatusId = 1L)
          )
          events.insertAll(testEvents: _*)

          val testTypAnswers = Seq(
            TypAnswer(code = "code1", name = "nom1", group = "Y"),
            TypAnswer(code = "code2", name = "nom2", group = "N")
          )
          typAnswers.insertAll(testTypAnswers: _*)

          val testPlayers = Seq(
            Player(firstName = "prenom1", lastName = "nom1", email = "email1"),
            Player(firstName = "prenom2", lastName = "nom2", email = "email2")
          )
          players.insertAll(testPlayers: _*)

          val testAnswers = Seq(
            Answer(Some(1), eventId = 1, playerId = 1, typAnswerId = 1),
            Answer(Some(2), eventId = 1, playerId = 2, typAnswerId = 2, comments = Some("Absent")),
            Answer(Some(3), eventId = 2, playerId = 1, typAnswerId = 1))
          answers.insertAll(testAnswers: _*)
          answers.list must equalTo(testAnswers)
      }
    }

  }

}
