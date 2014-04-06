package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, EventTeam}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:39
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class EventTeams(tag:Tag) extends Table[EventTeam](tag, "fam_event_team") {

  def eventId = column[Long]("id_event")

  def teamId = column[Long]("id_team")

  def * = (eventId , teamId)<>(EventTeam.tupled, EventTeam.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def team = foreignKey("TEAM_FK", teamId, TableQuery[Teams])(_.id)

  def event = foreignKey("EVENT_FK", eventId, TableQuery[Events])(_.id)


}
