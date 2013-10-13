package models.database


import play.api.db.slick.Config.driver.simple._
import models.Country

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:15
 * To change this template use File | Settings | File Templates.
 */

// define tables
private[models] object Countries extends Table[Country]("fam_country") {

  def id = column[Long]("id_country", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_country")

  def name = column[String]("lib_country")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def * = id.? ~ code ~ name ~ upper ~ lower <>(Country.apply _, Country.unapply _)

  def autoInc = * returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byUpper = createFinderBy(_.upper)
  val byLower = createFinderBy(_.lower)


}
