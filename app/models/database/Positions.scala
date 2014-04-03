package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Position}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:44
 * To change this template use File | Settings | File Templates.
 */

// define tables
  class Positions(tag:Tag) extends Table[Position](tag, "fam_position") {

  def id = column[Long]("id_position", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_position")

  def code = column[String]("cod_position")

  def * = (id.? , code , name )

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

}
