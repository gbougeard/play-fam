package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Group}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:00
 * To change this template use File | Settings | File Templates.
 */

// define tables
  class Groups(tag:Tag) extends Table[Group](tag, "fam_group") {

  def id = column[Long]("id_group", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_group")


  def * = (id.? , name)

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)


}
