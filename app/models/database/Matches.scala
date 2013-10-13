package models.database

import play.api.db.slick.Config.driver.simple._
import models.Match

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:03
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Matches extends Table[Match]("fam_match") {

  def id = column[Long]("idMatch", O.PrimaryKey, O.AutoInc)

  def fixtureId = column[Long]("id_fixture")

  def competitionId = column[Long]("id_season_competition")

  def eventId = column[Long]("id_event")

  def * = id.? ~ fixtureId.? ~ competitionId ~ eventId.? <>(Match.apply _, Match.unapply _)

  def autoInc = * returning id


  // A reified foreign key relation that can be navigated to create a join
  def fixture = foreignKey("FIXTURE_FK", fixtureId, Fixtures)(_.id)

  def competition = foreignKey("COMPETITION_FK", competitionId, SeasonCompetitions)(_.id)

  def event = foreignKey("EVENT_FK", eventId, Events)(_.id)

  val byId = createFinderBy(_.id)
  val byEventId = createFinderBy(_.eventId)

}
