package models.database

import play.api.db.slick.Config.driver.simple._
import models.Place

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:16
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Places extends Table[Place]("fam_place") {

  def id = column[Long]("id_place", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_place")

  def address = column[String]("address")

  def city = column[String]("city")

  def zipcode = column[String]("zipcode")

  def latitude = column[Float]("latitude")

  def longitude = column[Float]("longitude")

  def comments = column[String]("comments")

  def typFff = column[String]("typ_fff")

  def * = id.? ~ name ~ address ~ city ~ zipcode ~ latitude.? ~ longitude.? ~ comments.? ~ typFff.? <>(Place.apply _, Place.unapply _)

  def autoInc = * returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.zipcode)
  val byCity = createFinderBy(_.city)


}
