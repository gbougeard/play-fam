package models.database

import play.api.db.slick.Config.driver.simple._
import models.Season

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:20
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Seasons extends Table[Season]("fam_season") {

  def id = column[Long]("id_season", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_season")

  def currentSeason = column[Boolean]("current_season")

  def * = id.? ~ currentSeason ~ name <>(Season.apply _, Season.unapply _)

  def autoInc = * returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCurrentSeason = createFinderBy(_.currentSeason)


}
