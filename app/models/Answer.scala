package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class Answer(id: Option[Long] = None,
                  eventId: Long,
                  playerId: Long,
                  typAnswerId: Long,
                  comments: Option[String] = None)

object AnswerJson {

  import play.api.libs.json._

  implicit val answerJsonFormat = Json.format[Answer]


}

object AnswerExtJson {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._
  import models.EventJson._
  import models.PlayerJson._
  import models.TypAnswerJson._

  implicit val answerJsonFormat = Json.format[Answer]

  implicit val answerCompleteReads: Reads[(Answer, Event, Player, TypAnswer)] = (
    (__ \ 'answer).read[Answer] ~
      (__ \ 'event).read[Event] ~
      (__ \ 'player).read[Player] ~
      (__ \ 'typAnswer).read[TypAnswer]
    ) tupled

  implicit val answerCompleteWrites: Writes[(Answer, Event, Player, TypAnswer)] = (
    (__ \ 'answer).write[Answer] ~
      (__ \ 'event).write[Event] ~
      (__ \ 'player).write[Player] ~
      (__ \ 'typAnswer).write[TypAnswer]
    ) tupled

}

object Answers extends DAO{
  lazy val pageSize = 10

  def findByEvent(idEvent: Long): Seq[(Answer, Event, Player, TypAnswer)] =  DB.withSession {
    implicit session =>
      val query = for {a <- answers
                       if a.eventId === idEvent
                       e <- a.event
                       p <- a.player
                       ta <- a.typAnswer
      } yield (a, e, p, ta)
      query.list
  }

  def insert(answer: Answer): Long =  DB.withSession {
    implicit session =>
      answers.insert(answer)
  }

  def update(idMatch: Long, idPlayer: Long, answer: Answer) =  DB.withSession {
    implicit session =>
      //      val answer2update = answer.copy(Some(id))
      //      Logger.info("playe2update " + answer2update)
      //      Answers.where(_.id === id).update(answer2update)
  }

  def delete(idMatch: Long, idPlayer: Long) =  DB.withSession {
    implicit session =>
      //      Answers.where(_.id === answerId).delete
  }

}

