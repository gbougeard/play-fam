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

case class EventTeam(eventId: Long,
                     teamId: Long)

// define tables
object EventTeams extends Table[EventTeam]("fam_event_team") {

  def eventId = column[Long]("id_event")

  def teamId = column[Long]("id_team")

  def * = eventId ~ teamId <>(EventTeam, EventTeam.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

  def event = foreignKey("EVENT_FK", eventId, Events)(_.id)

  def findByEvent(id: Long): Seq[(EventTeam, Event, Team)] = DB.withSession {
    implicit session => {
      val query = (
        for {et <- EventTeams
             if et.eventId === id
             t <- et.team
             e <- et.event

        } yield (et, e, t))
      query.list
    }
  }

  def findByTeam(id: Long): Seq[(EventTeam, Event, Team)] = DB.withSession {
    implicit session => {
      val query = (
        for {et <- EventTeams
             if et.teamId === id
             t <- et.team
             e <- et.event

        } yield (et, e, t))
      query.list
    }
  }

  def insert(event: EventTeam):EventTeam = DB.withSession {
    implicit session => {
      Logger.debug("insert %s".format(event))
        EventTeams.insert((event))
    }
  }
  def insert(events: Seq[EventTeam]):Try[Option[Int]] = DB.withSession {
    implicit session => {
      Try(EventTeams.insertAll(events:_*))
    }
  }

  def deleteForEvent(id: Long) = DB.withSession {
    implicit session => {
      Logger.debug("delete %s".format(id))
      EventTeams.where(_.eventId === id).delete
    }
  }


  //  def delete(eId: Long, tId : Long) = DB.withSession {
  //    implicit session => {
  //      EventTeams.where(_.eventId === eId).and( _.teamId === tId).delete
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

