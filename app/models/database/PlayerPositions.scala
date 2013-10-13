package models.database

import play.api.db.slick.Config.driver.simple._
import models.PlayerPosition

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:38
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object PlayerPositions extends Table[PlayerPosition]("fam_player_position") {

  def playerId = column[Long]("id_player")

  def positionId = column[Long]("id_position")

  def numOrder = column[Int]("num_order")

  def * = playerId ~ positionId ~ numOrder <>(PlayerPosition.apply _, PlayerPosition.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def player = foreignKey("PLAYER_FK", playerId, Players)(_.id)

  def position = foreignKey("SEASON_FK", positionId, Positions)(_.id)


}
