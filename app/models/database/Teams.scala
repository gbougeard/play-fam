package models.database

import play.api.db.slick.Config.driver.simple._
import models.{Category, Team}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:36
 * To change this template use File | Settings | File Templates.
 */
// define tables
  class Teams(tag:Tag) extends Table[Team](tag, "fam_team") {

  def id = column[Long]("id_team", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_team")

  def code = column[String]("cod_team")

  def clubId = column[Long]("id_club")

  def * = (id.? , code , name , clubId ) <>(Team.tupled, Team.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def club = foreignKey("CLUB_FK", clubId, TableQuery[Clubs])(_.id)

}
