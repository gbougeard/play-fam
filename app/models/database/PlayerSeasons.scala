package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, PlayerSeason}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:41
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class PlayerSeasons(tag:Tag) extends Table[PlayerSeason](tag, "fam_player_season") {

  def playerId = column[Long]("id_player")

  def seasonId = column[Long]("id_season")

  def teamId = column[Long]("id_team")

  def clubId = column[Long]("id_club")

  def cap_att = column[Long]("att")

  def cap_def = column[Long]("def")

  def cap_phy = column[Long]("phy")

  def cap_pui = column[Long]("pui")

  def cap_tec = column[Long]("tec")

  def cap_vit = column[Long]("vit")

  def height = column[Double]("height")

  def weight = column[Double]("weight")

  def statAvgAssistPerMatch = column[Double]("avg_assist_per_match")

  def statAvgGoalPerMatch = column[Double]("avg_goal_per_match")

  def statAvgNote = column[Double]("avg_note")

  def statNbAssist = column[Long]("nb_assist")

  def statNbGoal = column[Long]("nb_goal")

  def statNbMatch = column[Long]("nb_match")

  def statNbSubstitue = column[Long]("nb_substitute")

  def statNbWorkout = column[Long]("nb_workout")

  def statTimePlayed = column[Long]("time_played")

  def * = (playerId , seasonId , teamId.? , clubId , cap_att.? , cap_def.? , cap_phy.? , cap_pui.? , cap_tec.? , cap_vit.? , height.? , weight.? , statAvgAssistPerMatch.? , statAvgGoalPerMatch.? , statAvgNote.? , statNbAssist.? , statNbGoal.? , statNbMatch.? , statNbSubstitue.? , statNbWorkout.? , statTimePlayed.? )

  // A reified foreign key relation that can be navigated to create a join
  def player = foreignKey("PLAYER_FK", playerId, TableQuery[Players])(_.id)

  def season = foreignKey("SEASON_FK", seasonId, TableQuery[Seasons])(_.id)

  def team = foreignKey("TEAM_FK", teamId, TableQuery[Teams])(_.id)

  def club = foreignKey("CLUB_FK", clubId, TableQuery[Clubs])(_.id)


}
