package models.database

import play.api.db.slick.Config.driver.simple._
import models.Club

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */

// define tables
private[models] object Clubs extends Table[Club]("fam_club") {

  def id = column[Long]("id_club", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_club")

  def code = column[Int]("code_fff")

  def countryId = column[Long]("id_country")

  def cityId = column[Long]("id_city")

  def colours = column[String]("colours")

  def address = column[String]("address")

  def zipcode = column[String]("zipcode")

  def city = column[String]("city")

  def organization = column[Long]("id_organization")

  def comments = column[String]("comments")

  def * = id.? ~ code ~ name ~ countryId.? ~ cityId.? ~ colours.? ~ address.? ~ zipcode.? ~ city.? ~ organization.? ~ comments.? <>(Club.apply _, Club.unapply _)

  def autoInc = * returning id


  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byZipCode = createFinderBy(_.zipcode)
  val byCity = createFinderBy(_.city)

}
