package models.database

import play.api.db.slick.Config.driver.simple._
import models.Province

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Provinces extends Table[Province]("fam_province") {

  def id = column[Long]("id_province", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_province")

  def name = column[String]("lib_province")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def stateId = column[Long]("id_state")

  def * = id.? ~ code ~ name ~ upper ~ lower ~ stateId <>(Province.apply _, Province.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def state = foreignKey("STATE_FK", stateId, States)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byUpper = createFinderBy(_.upper)
  val byLower = createFinderBy(_.lower)
  val byState = createFinderBy(_.stateId)


}
