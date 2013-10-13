package models

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.util.Date
import database.Fixtures

case class Fixture(id: Option[Long],
                date: Date,
                name: String,
                competitionId: Long) {
}

object Fixture{
  lazy val pageSize = 10

  def findAll: Seq[Fixture] = DB.withSession {
    implicit session:Session => {
      (for (c <- Fixtures.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(Fixtures.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Fixture, SeasonCompetition)] = {

    val offset = pageSize * page
    DB.withSession {
      implicit session:Session => {
        val fixtures = (
          for {t <- Fixtures
            .sortBy(club => orderField match {
            case 1 => club.date.asc
            case -1 => club.date.desc
            case 2 => club.name.asc
            case -2 => club.name.desc
          })
            .drop(offset)
            .take(pageSize)
               c <- t.competition

          } yield (t, c)).list

        Page(fixtures, page, offset, count)
      }
    }
  }

  def findById(id: Long): Option[Fixture] = DB.withSession {
    implicit session:Session => {
      Fixtures.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Fixture] = DB.withSession {
    implicit session:Session => {
      Fixtures.byName(name).firstOption
    }
  }

  def findByDate(date: Date): Option[Fixture] = DB.withSession {
    implicit session:Session => {
      Fixtures.byDate(date).firstOption
    }
  }

  def insert(fixture: Fixture): Long = DB.withSession {
    implicit session:Session => {
      Fixtures.autoInc.insert(fixture)
    }
  }

  def update(id: Long, fixture: Fixture) = DB.withSession {
    implicit session:Session => {
      val fixture2update = fixture.copy(Some(id), fixture.date, fixture.name, fixture.competitionId)
      Fixtures.where(_.id === id).update(fixture2update)
    }
  }

  def delete(fixtureId: Long) = DB.withSession {
    implicit session:Session => {
      Fixtures.where(_.id === fixtureId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- Fixtures
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val fixtureFormat = Json.format[Fixture]

}