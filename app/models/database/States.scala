package models.database

import play.api.db.slick.Config.driver.simple._
import models.State

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:30
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object States extends Table[State]("fam_state") {

  def id = column[Long]("id_state", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_state")

  def name = column[String]("lib_state")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def countryId = column[Long]("id_country")

  def * = id.? ~ code ~ name ~ upper ~ lower ~ countryId <>(State.apply _, State.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def country = foreignKey("COUNTRY_FK", countryId, Countries)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byUpper = createFinderBy(_.upper)
  val byLower = createFinderBy(_.lower)
  val byCountry = createFinderBy(_.countryId)


}
