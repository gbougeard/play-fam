package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, CompetitionTeam}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:11
 * To change this template use File | Settings | File Templates.
 */

// define tables
  class CompetitionTeams(tag:Tag) extends Table[CompetitionTeam](tag, "fam_competition_team") {

  def id = column[Long]("id_competition_team", O.PrimaryKey, O.AutoInc)

  def competitionId = column[Long]("id_season_competition")

  def teamId = column[Long]("id_team")

  def * = (id.? , competitionId , teamId )<>(CompetitionTeam.tupled, CompetitionTeam.unapply _)


  // A reified foreign key relation that can be navigated to create a join
  def competition = foreignKey("CT_COMPETITION_FK", competitionId, TableQuery[SeasonCompetitions])(_.id)

  def team = foreignKey("CT_TEAM_FK", teamId, TableQuery[Teams])(_.id)


}
