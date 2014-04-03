package models.database

import play.api.db.slick.Config.driver.simple._

import models.Category
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Categories(tag:Tag) extends Table[Category](tag, "fam_category") {

  def id = column[Long]("id_category", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_category")

  def code = column[String]("cod_category")

  def * = (id.? , code , name)

}
