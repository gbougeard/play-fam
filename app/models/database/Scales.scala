package models.database

import play.api.db.slick.Config.driver.simple._
import models.Scale

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Scales extends Table[Scale]("fam_scale") {

  def id = column[Long]("id_scale", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_scale")

  def code = column[String]("cod_scale")

  def ptsDefeat = column[Int]("pts_defeat")

  def ptsDraw = column[Int]("pts_draw")

  def ptsVictory = column[Int]("pts_victory")


  def * = id.? ~ code ~ name ~ ptsDefeat ~ ptsDraw ~ ptsVictory <>(Scale.apply _, Scale.unapply _)

  def autoInc = * returning id


  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)


}
