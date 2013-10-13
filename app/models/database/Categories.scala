package models.database

import play.api.db.slick.Config.driver.simple._

import models.Category

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Categories extends Table[Category]("fam_category") {

  def id = column[Long]("id_category", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_category")

  def code = column[String]("cod_category")

  def * = id.? ~ code ~ name  <>(Category.apply _, Category.unapply _)
  def autoInc = * returning id


  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

}
