package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._

// Use the implicit threadLocalSession
import scala.slick.session.Database.threadLocalSession

case class Season(id: Option[Long],
                  currentSeason: Boolean,
                  name: String)

// define tables
object Seasons extends Table[Season]("fam_season") {

  def id = column[Long]("id_season", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_season")

  def currentSeason = column[Boolean]("current_season")

  def * = id.? ~ currentSeason ~ name <>(Season, Season.unapply _)

  def autoInc = id.? ~ currentSeason ~ name <>(Season, Season.unapply _) returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCurrentSeason = createFinderBy(_.currentSeason)

  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val pageSize = 10


  def findAll: Seq[Season] = database withSession {
    (for (c <- Seasons.sortBy(_.name)) yield c).list
  }

  def findPage(page: Int = 0, orderField: Int): Page[Season] = {

    val offset = pageSize * page

    database withSession {
      val seasons = (
        for {c <- Seasons
          .sortBy(season => orderField match {
          case 1 => season.currentSeason.asc
          case -1 => season.currentSeason.desc
          case 2 => season.name.asc
          case -2 => season.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c).list

      val totalRows = (for (c <- Seasons) yield c.id).list.size
      Page(seasons, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[Season] = database withSession {
    Seasons.byId(id).firstOption
  }

  def insert(season: Season): Long = database withSession {
    Seasons.autoInc.insert((season))
  }

  def update(id: Long, season: Season) = database withSession {
    val season2update = season.copy(Some(id))
        Seasons.where(_.id === id).update(season2update)
  }

  def delete(seasonId: Long) = database withSession {
    Seasons.where(_.id === seasonId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  implicit val seasonFormat = Json.format[Season]

}



