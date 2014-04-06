package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Role}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Roles(tag:Tag) extends Table[Role](tag, "fam_user_group") {

  def userId = column[Long]("id_user")

  def groupId = column[Long]("id_group")


  def * = (userId , groupId )<>(Role.tupled, Role.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def user = foreignKey("USER_FK", userId, TableQuery[Users])(_.pid)

  def group = foreignKey("GROUP_FK", groupId, TableQuery[Groups])(_.id)

}
