package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Goal}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */

// define tables
  class Goals(tag:Tag) extends Table[Goal](tag, "fam_goal") {

  def id = column[Long]("id_goal", O.PrimaryKey, O.AutoInc)

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def strikerId = column[Long]("id_striker")

  def assistId = column[Long]("id_assist")

  def goalTime = column[Long]("goal_time")

  def penalty = column[Boolean]("penalty")

  def csc = column[Boolean]("csc")

  def * = (id.? , matchId , teamId , strikerId.? , assistId.? , goalTime.? , penalty , csc )<>(Goal.tupled, Goal.unapply _)


  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, TableQuery[Matches])(_.id)

  def team = foreignKey("TEAM_FK", teamId, TableQuery[Teams])(_.id)

  def striker = foreignKey("STRIKER_FK", strikerId, TableQuery[Players])(_.id)

  def assist = foreignKey("ASSIST_FK", assistId, TableQuery[Players])(_.id)


}
