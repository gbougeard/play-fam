package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Events._
import models.Players._
import models.TypAnswers._

import play.api.Logger

case class Answer(id: Option[Long],
                  eventId: Long,
                  playerId: Long,
                  typAnswerId: Long,
                  comments: Option[String])

// define tables
object Answers extends Table[Answer]("fam_answer") {

  def id = column[Long]("id_answer")

  def eventId = column[Long]("id_event")

  def playerId = column[Long]("id_player")

  def typAnswerId = column[Long]("id_typ_answer")

  def comments = column[String]("comments")

  def * = id.? ~ eventId ~ playerId ~ typAnswerId ~ comments.? <>(Answer, Answer.unapply _)

  def autoInc = id.? ~ eventId ~ playerId ~ typAnswerId ~ comments.? <>(Answer, Answer.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def event = foreignKey("EVENT_FK", eventId, Events)(_.id)

  def player = foreignKey("PLAYER_FK", playerId, Players)(_.id)

  def typAnswer = foreignKey("TYP_ANSWER_FK", typAnswerId, TypAnswers)(_.id)

  lazy val pageSize = 10

  def findByEvent(idEvent: Long): Seq[(Answer, Event, Player, TypAnswer)] = DB.withSession {
    implicit session => {
      val query = (
        for {a <- Answers
             if a.eventId === idEvent
             e <- a.event
             p <- a.player
             ta <- a.typAnswer
        } yield (a, e, p, ta))
      query.list
    }
  }

  def insert(answer: Answer): Long = DB.withSession {
    implicit session => {
      Answers.insert((answer))
    }
  }

  def update(idMatch: Long, idPlayer: Long, answer: Answer) = DB.withSession {
    implicit session => {
      //      val answer2update = answer.copy(Some(id))
      //      Logger.info("playe2update " + answer2update)
      //      Answers.where(_.id === id).update(answer2update)
    }
  }

  def delete(idMatch: Long, idPlayer: Long) = DB.withSession {
    implicit session => {
      //      Answers.where(_.id === answerId).delete
    }
  }

  implicit val answerFormat = Json.format[Answer]

  implicit val mpCompleteReads: Reads[(Answer, Event, Player, TypAnswer)] = (
    (__ \ 'answer).read[Answer] ~
      (__ \ 'event).read[Event] ~
      (__ \ 'player).read[Player] ~
      (__ \ 'typanswer).read[TypAnswer]
    ) tupled

  implicit val mpCompleteWrites: Writes[(Answer, Event, Player, TypAnswer)] = (
    (__ \ 'answer).write[Answer] ~
      (__ \ 'event).write[Event] ~
      (__ \ 'player).write[Player] ~
      (__ \ 'typanswer).write[TypAnswer]
    ) tupled

}