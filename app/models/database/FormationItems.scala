package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, FormationItem}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:47
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class FormationItems(tag:Tag) extends Table[FormationItem](tag, "fam_formation_item") {

  def id = column[Long]("id_formation_item", O.PrimaryKey, O.AutoInc)

  def coord = column[Int]("coord")

  def numItem = column[Int]("num_item")

  def formationId = column[Long]("id_formation")

  def * = (id.? , coord , numItem , formationId )


  // A reified foreign key relation that can be navigated to create a join
  def formation = foreignKey("FORMATION_FK", formationId, TableQuery[Formations])(_.id)


}
