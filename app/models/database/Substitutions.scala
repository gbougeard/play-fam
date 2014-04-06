package models.database

import play.api.db.slick.Config.driver.simple._
import models.Substitution
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:34
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Substitutions(tag:Tag) extends Table[Substitution](tag, "fam_substitution") {

  def id = column[Long]("id_substitution")

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def playerInId = column[Long]("id_player_in")

  def playerOutId = column[Long]("id_player_out")

  def time = column[Long]("substitution_time")

  def * = (id.? , matchId , teamId , playerInId , playerOutId , time.? )<>(Substitution.tupled, Substitution.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, TableQuery[Matches])(_.id)

  def playerIn = foreignKey("PLAYER_IN_FK", playerInId, TableQuery[Players])(_.id)

  def playerOut = foreignKey("PLAYER_OUT_FK", playerOutId, TableQuery[Players])(_.id)

  def team = foreignKey("TEAM_FK", teamId, TableQuery[Teams])(_.id)


}
