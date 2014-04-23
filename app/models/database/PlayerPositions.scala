package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, PlayerPosition}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:38
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class PlayerPositions(tag:Tag) extends Table[PlayerPosition](tag, "fam_player_position") {

  def playerId = column[Long]("id_player")

  def positionId = column[Long]("id_position")

  def numOrder = column[Int]("num_order")

  def * = (playerId , positionId , numOrder )<>(PlayerPosition.tupled, PlayerPosition.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def player = foreignKey("PP_PLAYER_FK", playerId, TableQuery[Players])(_.id)

  def position = foreignKey("PP_SEASON_FK", positionId, TableQuery[Positions])(_.id)


}
