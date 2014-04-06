package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, MatchPlayer, Match}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:09
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class MatchPlayers(tag:Tag) extends Table[MatchPlayer](tag, "fam_match_player") {

  def matchId = column[Long]("id_match")

  def playerId = column[Long]("id_player")

  def teamId = column[Long]("id_team")

  def num = column[Long]("num")

  def captain = column[Boolean]("captain")

  def note = column[Double]("note")

  def timePlayed = column[Long]("time_played")

  def comments = column[String]("comments")

  def * = (matchId.? , playerId.? , teamId.? , num.? , captain , note.? , timePlayed.? , comments.? )<>(MatchPlayer.tupled, MatchPlayer.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, TableQuery[Matches])(_.id)

  def player = foreignKey("PLAYER_FK", playerId, TableQuery[Players])(_.id)

  def team = foreignKey("TEAM_FK", teamId, TableQuery[Teams])(_.id)


}
