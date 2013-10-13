package models.database

import play.api.db.slick.Config.driver.simple._
import models.Formation

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Formations extends Table[Formation]("fam_formation") {

  def id = column[Long]("id_formation", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_formation")

  def code = column[String]("cod_formation")

  def isDefault = column[Boolean]("byDefault")

  def typMatchId = column[Long]("id_typ_match")

  def * = id.? ~ code ~ name ~ isDefault ~ typMatchId <>(Formation.apply _, Formation.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)


}
