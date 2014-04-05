package models

import play.api.Play.current
import play.Logger

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Events._
import models.Teams._
import scala.util.Try
import database.EventTeams

case class EventTeam(eventId: Long,
                     teamId: Long)

object EventTeams extends DAO{
  def findByEvent(id: Long): Seq[(EventTeam, Event, Team)] =  DB.withSession {
    implicit session =>
      val query = for {et <- eventTeams
                       if et.eventId === id
                       t <- et.team
                       e <- et.event

      } yield (et, e, t)
      query.list
  }

  def findByTeam(id: Long): Seq[(EventTeam, Event, Team)] =  DB.withSession {
    implicit session =>
      val query = for {et <- eventTeams
                       if et.teamId === id
                       t <- et.team
                       e <- et.event

      } yield (et, e, t)
      query.list
  }

  def insert(event: EventTeam):Int =  DB.withSession {
    implicit session =>
        eventTeams.insert(event)
  }
  def insert(events: Seq[EventTeam]):Try[Option[Int]] =  DB.withSession {
    implicit session =>
      Try(eventTeams.insertAll(events:_*))
  }

  def deleteForEvent(id: Long) =  DB.withSession {
    implicit session =>
      eventTeams.where(_.eventId === id).delete
  }


  //  def delete(eId: Long, tId : Long) =  {
  //    implicit session:Session => {
  //      eventTeams.where(_.eventId === eId).and( _.teamId === tId).delete
  //    }
  //  }


  implicit val eventTeamFormat = Json.format[EventTeam]

  implicit val eventTeamCompleteReads: Reads[(EventTeam, Event, Team)] = (
    (__ \ 'eventteam).read[EventTeam] ~
      (__ \ 'event).read[Event] ~
      (__ \ 'team).read[Team]
    ) tupled

  implicit val eventTeamCompleteWrites: Writes[(EventTeam, Event, Team)] = (
    (__ \ 'eventteam).write[EventTeam] ~
      (__ \ 'event).write[Event] ~
      (__ \ 'team).write[Team]
    ) tupled

}

