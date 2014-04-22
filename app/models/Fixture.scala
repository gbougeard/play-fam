package models

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._

import org.joda.time.DateTime
import play.api.db.slick.joda.PlayJodaSupport
import play.api.db.slick.joda.PlayJodaSupport._

case class Fixture(id: Option[Long],
                date: DateTime,
                name: String,
                competitionId: Long) {
}

object Fixtures extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[Fixture] =  DB.withSession {
    implicit session =>
      (for (c <- fixtures.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      fixtures.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Fixture, SeasonCompetition)] = DB.withSession {
    implicit session =>

    val offset = pageSize * page
        val q = for {t <- fixtures
          .sortBy(club => orderField match {
          case 1 => club.date.asc
          case -1 => club.date.desc
          case 2 => club.name.asc
          case -2 => club.name.desc
        })
          .drop(offset)
          .take(pageSize)
                     c <- t.competition

        } yield (t, c)

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[Fixture] =  DB.withSession {
    implicit session =>
      fixtures.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[Fixture] =  DB.withSession {
    implicit session =>
      fixtures.where(_.name === name).firstOption
  }

  def findByDate(date: DateTime): Option[Fixture] =  DB.withSession {
    implicit session =>
      fixtures.where(_.date === date).firstOption
  }

  def insert(fixture: Fixture): Long =  DB.withSession {
    implicit session =>
      fixtures.insert(fixture)
  }

  def update(id: Long, fixture: Fixture) =  DB.withSession {
    implicit session =>
      val fixture2update = fixture.copy(Some(id), fixture.date, fixture.name, fixture.competitionId)
      fixtures.where(_.id === id).update(fixture2update)
  }

  def delete(fixtureId: Long) =  DB.withSession {
    implicit session =>
      fixtures.where(_.id === fixtureId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- fixtures
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}