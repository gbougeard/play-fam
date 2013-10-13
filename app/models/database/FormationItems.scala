package models.database

import play.api.db.slick.Config.driver.simple._
import models.FormationItem

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object FormationItems extends Table[FormationItem]("fam_formation_item") {

  def id = column[Long]("id_formation_item", O.PrimaryKey, O.AutoInc)

  def coord = column[Int]("coord")

  def numItem = column[Int]("num_item")

  def formationId = column[Long]("id_formation")

  def * = id.? ~ coord ~ numItem ~ formationId <>(FormationItem.apply _, FormationItem.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def formation = foreignKey("FORMATION_FK", formationId, Formations)(_.id)


}
