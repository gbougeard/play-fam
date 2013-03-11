package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

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

  lazy val pageSize = 10


  def findAll: Seq[Season] = DB.withSession {
    implicit session => {
      (for (c <- Seasons.sortBy(_.name)) yield c).list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Season] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session => {
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
  }

  def findById(id: Long): Option[Season] = DB.withSession {
    implicit session => {
      Seasons.byId(id).firstOption
    }
  }

  def insert(season: Season): Long = DB.withSession {
    implicit session => {
      Seasons.autoInc.insert((season))
    }
  }

  def update(id: Long, season: Season) = DB.withSession {
    implicit session => {
      val season2update = season.copy(Some(id))
      Seasons.where(_.id === id).update(season2update)
    }
  }

  def delete(seasonId: Long) = DB.withSession {
    implicit session => {
      Seasons.where(_.id === seasonId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString(), c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Seasons
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val seasonFormat = Json.format[Season]

}



