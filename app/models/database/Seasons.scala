package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Season}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Seasons(tag:Tag) extends Table[Season](tag, "fam_season") {

  def id = column[Long]("id_season", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_season")

  def currentSeason = column[Boolean]("current_season")

  def * = (id.? , currentSeason , name )

}
