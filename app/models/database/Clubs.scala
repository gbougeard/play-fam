package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Club}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */

// define tables
  class Clubs(tag:Tag) extends Table[Club](tag, "fam_club") {

  def id = column[Long]("id_club", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_club")

  def code = column[Int]("code_fff")

  def countryId = column[Option[Long]]("id_country")

  def cityId = column[Option[Long]]("id_city")

  def colours = column[Option[String]]("colours")

  def address = column[Option[String]]("address")

  def zipcode = column[Option[String]]("zipcode")

  def city = column[Option[String]]("city")

  def organization = column[Option[Long]]("id_organization")

  def comments = column[Option[String]]("comments")

  def * = (id.? , code , name , countryId , cityId , colours , address , zipcode , city , organization , comments )<>(Club.tupled, Club.unapply _)


}
