package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Scale}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Scales(tag:Tag) extends Table[Scale](tag, "fam_scale") {

  def id = column[Long]("id_scale", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_scale")

  def code = column[String]("cod_scale")

  def ptsDefeat = column[Int]("pts_defeat")

  def ptsDraw = column[Int]("pts_draw")

  def ptsVictory = column[Int]("pts_victory")


  def * = (id.? , code , name , ptsDefeat , ptsDraw , ptsVictory )<>(Scale.tupled, Scale.unapply _)



}
