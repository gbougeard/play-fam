package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, PlaceClub}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:21
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class PlaceClubs(tag:Tag) extends Table[PlaceClub](tag, "fam_club_place") {

  def placeId = column[Long]("id_place")

  def clubId = column[Long]("id_club")

  def * = (placeId , clubId )<>(PlaceClub.tupled, PlaceClub.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def place = foreignKey("PLACE_FK", placeId, TableQuery[Places])(_.id)

  def club = foreignKey("CLUB_FK", clubId, TableQuery[Clubs])(_.id)

}
