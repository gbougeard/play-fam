package models.database

import play.api.db.slick.Config.driver.simple._
import models.MatchTeam

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:12
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object MatchTeams extends Table[MatchTeam]("fam_match_team") {

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def formationId = column[Long]("id_formation")

  def home = column[Boolean]("home")

  def defeat = column[Boolean]("defeat")

  def draw = column[Boolean]("draw")

  def victory = column[Boolean]("victory")

  def goalScored = column[Long]("goal_scored")

  def goalShipped = column[Long]("goal_shipped")

  def points = column[Long]("points")

  def resume = column[String]("resume")

  def draft = column[Boolean]("draft")

  def * = matchId.? ~
    teamId.? ~
    formationId.? ~
    home ~
    defeat.? ~
    draw.? ~
    victory.? ~
    goalScored.? ~
    goalShipped.? ~
    points.? ~
    resume.? ~
    draft.? <>(MatchTeam.apply _, MatchTeam.unapply _)


  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, Matches)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

  //  def formation = foreignKey("FORMATION_FK", formationId, Events)(_.id)


}
