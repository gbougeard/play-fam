package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._

import Clubs._

// Use the implicit threadLocalSession
import scala.slick.session.Database.threadLocalSession

case class Team(id: Option[Long],
                code: String,
                name: String,
                clubId: Long) {
}

// define tables
object Teams extends Table[Team]("fam_team") {

  def id = column[Long]("id_team", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_team")

  def code = column[String]("cod_team")

  def clubId = column[Long]("id_club")

  def * = id.? ~ code ~ name ~ clubId <>(Team, Team.unapply _)

  def autoInc = id.? ~ code ~ name ~ clubId <>(Team, Team.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def club = foreignKey("CLUB_FK", clubId, Clubs)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val pageSize = 10

  def findAll: Seq[Team] = database withSession {
    (for (c <- Teams.sortBy(_.name)) yield c).list
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Team, Club)] = {

    val offset = pageSize * page
    database withSession {
      val teams = (
        for {t <- Teams
          .sortBy(club => orderField match {
          case 1 => club.code.asc
          case -1 => club.code.desc
          case 2 => club.name.asc
          case -2 => club.name.desc
        })
          .drop(offset)
          .take(pageSize)
             c <- t.club

        } yield (t, c)).list

      val totalRows = (for {t <- Teams} yield t.id).list.size
      Page(teams, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[Team] = database withSession {
    Teams.byId(id).firstOption
  }

  def findByName(name: String): Option[Team] = database withSession {
    Teams.byName(name).firstOption
  }

  def findByCode(code: String): Option[Team] = database withSession {
    Teams.byCode(code).firstOption
  }

  def insert(team: Team): Long = database withSession {
    Teams.autoInc.insert((team))
  }

  def update(id: Long, team: Team) = database withSession {
    val team2update = team.copy(Some(id), team.code, team.name, team.clubId)
    Teams.where(_.id === id).update(team2update)
  }

  def delete(teamId: Long) = database withSession {
    Teams.where(_.id === teamId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  implicit val teamFormat = Json.format[Team]

}