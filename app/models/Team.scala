package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import Clubs._

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
  val byClub = createFinderBy(_.clubId)

  lazy val pageSize = 10

  def findAll: Seq[Team] = DB.withSession {
    implicit session => {
      (for (c <- Teams.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      Query(Teams.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Team, Club)] = {

    val offset = pageSize * page
    DB.withSession {
      implicit session => {
        val teams = (
          for {t <- Teams
               c <- t.club
          } yield (t, c)
          ).sortBy(orderField match {
          case 1 => _._1.code.asc
          case -1 => _._1.code.desc
          case 2 => _._1.name.asc
          case -2 => _._1.name.desc
          case 3 => _._2.name.asc
          case -3 => _._2.name.desc
        })
          .drop(offset)
          .take(pageSize)

        Page(teams.list, page, offset, count)
      }
    }
  }

  def findById(id: Long): Option[Team] = DB.withSession {
    implicit session => {
      Teams.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Team] = DB.withSession {
    implicit session => {
      Teams.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Team] = DB.withSession {
    implicit session => {
      Teams.byCode(code).firstOption
    }
  }

  def findByClub(id: Long): Seq[Team] = DB.withSession {
    implicit session => {
      Teams.byClub(id).list
    }
  }

  def insert(team: Team): Long = DB.withSession {
    implicit session => {
      Teams.autoInc.insert((team))
    }
  }

  def update(id: Long, team: Team) = DB.withSession {
    implicit session => {
      val team2update = team.copy(Some(id), team.code, team.name, team.clubId)
      Teams.where(_.id === id).update(team2update)
    }
  }

  def delete(teamId: Long) = DB.withSession {
    implicit session => {
      Teams.where(_.id === teamId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Teams
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  def findByClubOptions(id: Long): Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Teams
        if (item.clubId is id)
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }


  implicit val teamFormat = Json.format[Team]

}