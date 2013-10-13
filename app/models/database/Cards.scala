package models.database

import play.api.db.slick.Config.driver.simple._
import models.Card

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Cards extends Table[Card]("fam_card") {

  def id = column[Long]("id_card")

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def playerId = column[Long]("id_player")

  def typCardId = column[Long]("id_typ_card")

  def time = column[Long]("card_time")

  def * = id.? ~ matchId ~ teamId ~ playerId ~ typCardId ~ time.? <>(Card.apply _, Card.unapply _)
  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, Matches)(_.id)

  def player = foreignKey("PLAYER_FK", playerId, Players)(_.id)

  def typCard = foreignKey("TYP_CARD_FK", typCardId, TypCards)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

}
