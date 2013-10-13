package models.database

import play.api.db.slick.Config.driver.simple._
import models.Role

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:15
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Roles extends Table[Role]("fam_user_group") {

  def userId = column[Long]("id_user")

  def groupId = column[Long]("id_group")


  def * = userId ~ groupId <>(Role.apply _, Role.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def user = foreignKey("USER_FK", userId, Users)(_.pid)

  def group = foreignKey("GROUP_FK", groupId, Groups)(_.id)

}
