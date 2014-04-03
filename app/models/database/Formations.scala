package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Formation}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Formations(tag:Tag) extends Table[Formation](tag, "fam_formation") {

  def id = column[Long]("id_formation", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_formation")

  def code = column[String]("cod_formation")

  def isDefault = column[Boolean]("byDefault")

  def typMatchId = column[Long]("id_typ_match")

  def * = (id.? , code , name , isDefault , typMatchId )

  // A reified foreign key relation that can be navigated to create a join
  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TableQuery[TypMatches])(_.id)

}
