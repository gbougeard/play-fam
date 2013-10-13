package models.database

import play.api.db.slick.Config.driver.simple._
import models.Position

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:44
 * To change this template use File | Settings | File Templates.
 */

// define tables
private[models] object Positions extends Table[Position]("fam_position") {

  def id = column[Long]("id_position", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_position")

  def code = column[String]("cod_position")

  def * = id.? ~ code ~ name <>(Position.apply _, Position.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)


}
