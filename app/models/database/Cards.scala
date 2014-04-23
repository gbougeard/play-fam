package models.database

import play.api.db.slick.Config.driver.simple._
import models.Card
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 16:56
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Cards(tag:Tag) extends Table[Card](tag, "fam_card") {

  def id = column[Long]("id_card", O.PrimaryKey, O.AutoInc)

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def playerId = column[Long]("id_player")

  def typCardId = column[Long]("id_typ_card")

  def time = column[Option[Long]]("card_time")

  def * = (id.? , matchId , teamId , playerId , typCardId , time)<>(Card.tupled, Card.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("CARD_MATCH_FK", matchId, TableQuery[Matches])(_.id)

  def player = foreignKey("CARD_PLAYER_FK", playerId, TableQuery[Players])(_.id)

  def typCard = foreignKey("TYP_CARD_FK", typCardId, TableQuery[TypCards])(_.id)

  def team = foreignKey("CARD_TEAM_FK", teamId, TableQuery[Teams])(_.id)

}
