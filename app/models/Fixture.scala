package models

import play.api.Play.current

import play.api.db.slick.DB
import play.api.db.slick.Config.driver.simple._
import slick.lifted.{Join, MappedTypeMapper}

import play.api.libs.json._
import play.api.libs.functional.syntax._

import java.util.Date

case class Fixture(id: Option[Long],
                date: Date,
                name: String,
                competitionId: Long) {
}

// define tables
object Fixtures extends Table[Fixture]("fam_fixture") {

  implicit val javaUtilDateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Date](
    x => new java.sql.Date(x.getTime),
    x => new java.util.Date(x.getTime)
  )

  def id = column[Long]("id_fixture", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_fixture")

  def date = column[Date]("dt_fixture")

  def competitionId = column[Long]("id_season_competition")

  def * = id.? ~ date ~ name ~ competitionId <>(Fixture, Fixture.unapply _)

  def autoInc = id.? ~ date ~ name ~ competitionId <>(Fixture, Fixture.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def competition = foreignKey("COMPETTITION_FK", competitionId, SeasonCompetitions)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byDate = createFinderBy(_.date)

  lazy val pageSize = 10

  def findAll: Seq[Fixture] = DB.withSession {
    implicit session => {
      (for (c <- Fixtures.sortBy(_.name)) yield c).list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Fixture, SeasonCompetition)] = {

    val offset = pageSize * page
    DB.withSession {
      implicit session => {
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

        val totalRows = (for {t <- Fixtures} yield t.id).list.size
        Page(fixtures, page, offset, totalRows)
      }
    }
  }

  def findById(id: Long): Option[Fixture] = DB.withSession {
    implicit session => {
      Fixtures.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Fixture] = DB.withSession {
    implicit session => {
      Fixtures.byName(name).firstOption
    }
  }

  def findByDate(date: Date): Option[Fixture] = DB.withSession {
    implicit session => {
      Fixtures.byDate(date).firstOption
    }
  }

  def insert(fixture: Fixture): Long = DB.withSession {
    implicit session => {
      Fixtures.autoInc.insert((fixture))
    }
  }

  def update(id: Long, fixture: Fixture) = DB.withSession {
    implicit session => {
      val fixture2update = fixture.copy(Some(id), fixture.date, fixture.name, fixture.competitionId)
      Fixtures.where(_.id === id).update(fixture2update)
    }
  }

  def delete(fixtureId: Long) = DB.withSession {
    implicit session => {
      Fixtures.where(_.id === fixtureId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Fixtures
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val fixtureFormat = Json.format[Fixture]

}