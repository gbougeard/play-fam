package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import database.Answers
import models.Event._
import models.Player._
import models.TypAnswer._

import play.api.Logger

case class Answer(id: Option[Long],
                  eventId: Long,
                  playerId: Long,
                  typAnswerId: Long,
                  comments: Option[String])

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

  implicit val answerFormat = Json.format[Answer]

  implicit val mpCompleteReads: Reads[(Answer, Event, Player, TypAnswer)] = (
    (__ \ 'answer).read[Answer] ~
      (__ \ 'event).read[Event] ~
      (__ \ 'player).read[Player] ~
      (__ \ 'typAnswer).read[TypAnswer]
    ) tupled

  implicit val mpCompleteWrites: Writes[(Answer, Event, Player, TypAnswer)] = (
    (__ \ 'answer).write[Answer] ~
      (__ \ 'event).write[Event] ~
      (__ \ 'player).write[Player] ~
      (__ \ 'typAnswer).write[TypAnswer]
    ) tupled

}