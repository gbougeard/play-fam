package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Player}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:34
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Players(tag:Tag) extends Table[Player](tag, "fam_player") {

  def id = column[Long]("id_player", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def email = column[String]("email")

  def userId = column[Option[Long]]("id_user")

  def * = (id.? , firstName , lastName , email , userId )<>(Player.tupled, Player.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def user = foreignKey("USER_FK", userId, TableQuery[Users])(_.pid)

}
