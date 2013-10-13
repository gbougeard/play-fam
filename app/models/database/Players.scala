package models.database

import play.api.db.slick.Config.driver.simple._
import models.Player

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 18:34
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Players extends Table[Player]("fam_player") {

  def id = column[Long]("id_player", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def email = column[String]("email")

  def userId = column[Long]("id_user")

  def * = id.? ~ firstName ~ lastName ~ email ~ userId.? <>(Player.apply _, Player.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def user = foreignKey("USER_FK", userId, Users)(_.pid)

  val byId = createFinderBy(_.id)
  val byFirstName = createFinderBy(_.firstName)
  val byLastName = createFinderBy(_.lastName)
  val byUserId = createFinderBy(_.userId)


}
