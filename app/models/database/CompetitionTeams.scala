package models.database

import play.api.db.slick.Config.driver.simple._
import models.CompetitionTeam

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */

// define tables
private[models] object CompetitionTeams extends Table[CompetitionTeam]("fam_competition_team") {

  def id = column[Long]("id_competition_team", O.PrimaryKey, O.AutoInc)

  def competitionId = column[Long]("id_season_competition")

  def teamId = column[Long]("id_team")

  def * = id.? ~ competitionId ~ teamId <>(CompetitionTeam.apply _, CompetitionTeam.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def competition = foreignKey("COMPETITION_FK", competitionId, SeasonCompetitions)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)


}
