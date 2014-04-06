package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Province}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Provinces(tag:Tag) extends Table[Province](tag, "fam_province") {

  def id = column[Long]("id_province", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_province")

  def name = column[String]("lib_province")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def stateId = column[Long]("id_state")

  def * = (id.? , code , name , upper , lower , stateId )<>(Province.tupled, Province.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def state = foreignKey("STATE_FK", stateId, TableQuery[States])(_.id)

}
