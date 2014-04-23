package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Match}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Matches(tag:Tag) extends Table[Match](tag, "fam_match") {

  def id = column[Long]("idMatch", O.PrimaryKey, O.AutoInc)

  def fixtureId = column[Long]("id_fixture")

  def competitionId = column[Long]("id_season_competition")

  def eventId = column[Long]("id_event")

  def * = (id.? , fixtureId.? , competitionId , eventId.? )<>(Match.tupled, Match.unapply _)


  // A reified foreign key relation that can be navigated to create a join
  def fixture = foreignKey("FIXTURE_FK", fixtureId, TableQuery[Fixtures])(_.id)

  def competition = foreignKey("M_COMPETITION_FK", competitionId, TableQuery[SeasonCompetitions])(_.id)

  def event = foreignKey("M_EVENT_FK", eventId, TableQuery[Events])(_.id)

}
