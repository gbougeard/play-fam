package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Ranking}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:48
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Rankings(tag:Tag) extends Table[Ranking](tag, "v_rankings") {

  def competitionId = column[Long]("id_season_competition")

  def clubId = column[Long]("id_club")

  def teamId = column[Long]("id_team")

  def team = column[String]("lib_team")

  def points = column[Int]("points")

  def victory = column[Int]("victory")

  def defeat = column[Int]("defeat")

  def draw = column[Int]("draw")

  def played = column[Int]("played")

  def goalScored = column[Int]("goal_scored")

  def goalShipped = column[Int]("goal_shipped")

  def * = (competitionId , clubId , teamId , team , points , victory , defeat , draw , played , goalScored , goalShipped )<>(Ranking.tupled, Ranking.unapply _)


}
