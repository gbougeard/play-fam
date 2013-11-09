package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import database.Teams
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import scala.Some

case class Team(id: Option[Long],
                code: String,
                name: String,
                clubId: Long) {
}

object Team{
  lazy val pageSize = 10

  def findAll: Seq[Team] = DB.withSession {
    implicit session:Session => {
      (for (c <- Teams.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(Teams.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Team, Club)] = {

    val offset = pageSize * page
    DB.withSession {
      implicit session:Session => {
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
    implicit session:Session => {
      Teams.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Team] = DB.withSession {
    implicit session:Session => {
      Teams.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Team] = DB.withSession {
    implicit session:Session => {
      Teams.byCode(code).firstOption
    }
  }

  def findByClub(id: Long): Seq[Team] = DB.withSession {
    implicit session:Session => {
      Teams.byClub(id).list
    }
  }

  def insert(team: Team): Long = DB.withSession {
    implicit session:Session => {
      Teams.autoInc.insert(team)
    }
  }

  def update(id: Long, team: Team) = DB.withSession {
    implicit session:Session => {
      val team2update = team.copy(Some(id), team.code, team.name, team.clubId)
      Teams.where(_.id === id).update(team2update)
    }
  }

  def delete(teamId: Long) = DB.withSession {
    implicit session:Session => {
      Teams.where(_.id === teamId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- Teams
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  def findByClubOptions(id: Long): Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- Teams
        if item.clubId is id
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }


  implicit val teamFormat = Json.format[Team]

}

trait TeamGen {

  lazy val genTeam: Gen[Team] = for {
    id <- arbitrary[Long]
    code <- arbitrary[String]
    name <- arbitrary[String]
    clubId <- arbitrary[Long]
  } yield Team(
      Some(id),
      code,
      name,
      clubId
    )

  implicit lazy val arbTeam: Arbitrary[Team] = Arbitrary(genTeam)
}
