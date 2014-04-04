package models

import play.api.Play.current
import play.Logger

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Event._
import models.Team._
import scala.util.Try
import database.EventTeams

case class EventTeam(eventId: Long,
                     teamId: Long)

object EventTeams extends DAO{
  def findByEvent(id: Long)(implicit session: Session): Seq[(EventTeam, Event, Team)] =  {
      val query = for {et <- eventTeams
                       if et.eventId === id
                       t <- et.team
                       e <- et.event

      } yield (et, e, t)
      query.list
  }

  def findByTeam(id: Long)(implicit session: Session): Seq[(EventTeam, Event, Team)] =  {
      val query = for {et <- eventTeams
                       if et.teamId === id
                       t <- et.team
                       e <- et.event

      } yield (et, e, t)
      query.list
  }

  def insert(event: EventTeam)(implicit session: Session):Int =  {
        eventTeams.insert(event)
  }
  def insert(events: Seq[EventTeam])(implicit session: Session):Try[Option[Int]] =  {
      Try(eventTeams.insertAll(events:_*))
  }

  def deleteForEvent(id: Long)(implicit session: Session) =  {
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

