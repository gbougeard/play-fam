package models.database

import play.api.db.slick.Config.driver.simple._
import models.City

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:04
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Cities extends Table[City]("fam_city") {

  def id = column[Long]("id_city", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_city")

  def name = column[String]("lib_city")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def provinceId = column[Long]("id_province")

  def * = id.? ~ code ~ name ~ upper ~ lower ~ provinceId <>(City.apply _, City.unapply _)

  def autoInc = * returning id


  // A reified foreign key relation that can be navigated to create a join
  def province = foreignKey("PROVINCE_FK", provinceId, Provinces)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byUpper = createFinderBy(_.upper)
  val byLower = createFinderBy(_.lower)
  val byProvince = createFinderBy(_.provinceId)


}
