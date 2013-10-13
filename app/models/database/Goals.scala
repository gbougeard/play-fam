package models.database

import play.api.db.slick.Config.driver.simple._
import models.Goal

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */

// define tables
private[models] object Goals extends Table[Goal]("fam_goal") {

  def id = column[Long]("id_goal", O.PrimaryKey, O.AutoInc)

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def strikerId = column[Long]("id_striker")

  def assistId = column[Long]("id_assist")

  def goalTime = column[Long]("goal_time")

  def penalty = column[Boolean]("penalty")

  def csc = column[Boolean]("csc")

  def * = id.? ~ matchId ~ teamId ~ strikerId.? ~ assistId.? ~ goalTime.? ~ penalty ~ csc <>(Goal.apply _, Goal.unapply _)

  def autoInc = * returning id


  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, Matches)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

  def striker = foreignKey("STRIKER_FK", strikerId, Players)(_.id)

  def assist = foreignKey("ASSIST_FK", assistId, Players)(_.id)


}
