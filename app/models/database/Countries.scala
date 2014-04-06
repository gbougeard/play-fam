package models.database


import play.api.db.slick.Config.driver.simple._
import models.{Category, Country}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:15
 * To change this template use File | Settings | File Templates.
 */

// define tables
  class Countries(tag:Tag) extends Table[Country](tag, "fam_country") {

  def id = column[Long]("id_country", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_country")

  def name = column[String]("lib_country")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def * = (id.? , code , name , upper , lower )<>(Country.tupled, Country.unapply _)


}
