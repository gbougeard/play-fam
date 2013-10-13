package models.database

import play.api.db.slick.Config.driver.simple._
import models.PlaceClub

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object PlaceClubs extends Table[PlaceClub]("fam_club_place") {

  def placeId = column[Long]("id_place")

  def clubId = column[Long]("id_club")

  def * = placeId ~ clubId <>(PlaceClub.apply _, PlaceClub.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def place = foreignKey("PLACE_FK", placeId, Places)(_.id)

  def club = foreignKey("CLUB_FK", clubId, Clubs)(_.id)

}
