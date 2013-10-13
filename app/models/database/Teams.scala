package models.database

import play.api.db.slick.Config.driver.simple._
import models.Team

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:36
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Teams extends Table[Team]("fam_team") {

  def id = column[Long]("id_team", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_team")

  def code = column[String]("cod_team")

  def clubId = column[Long]("id_club")

  def * = id.? ~ code ~ name ~ clubId <>(Team.apply _, Team.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def club = foreignKey("CLUB_FK", clubId, Clubs)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byClub = createFinderBy(_.clubId)


}
