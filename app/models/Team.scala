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

object Teams extends DAO{
  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[Team] =  {
      (for (c <- teams.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      (teams.length).run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(Team, Club)] = {

    val offset = pageSize * page
        val q = (
          for {t <- teams
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

        Page(q.list, page, offset, count)
  }

  def findById(id: Long)(implicit session: Session): Option[Team] =  {
      teams.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[Team] =  {
      teams.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[Team] =  {
      teams.where(_.code === code).firstOption
  }

  def findByClub(id: Long)(implicit session: Session): Seq[Team] =  {
      teams.where(_.clubId === id).list
  }

  def insert(team: Team)(implicit session: Session): Long =  {
      teams.insert(team)
  }

  def update(id: Long, team: Team)(implicit session: Session) =  {
      val team2update = team.copy(Some(id), team.code, team.name, team.clubId)
      teams.where(_.id === id).update(team2update)
  }

  def delete(teamId: Long)(implicit session: Session) =  {
      teams.where(_.id === teamId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- teams
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  def findByClubOptions(id: Long)(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- teams
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
