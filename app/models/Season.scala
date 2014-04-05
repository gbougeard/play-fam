package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._
import database.Seasons

case class Season(id: Option[Long],
                  currentSeason: Boolean,
                  name: String)

object Seasons extends DAO{
  lazy val pageSize = 10


  def findAll: Seq[Season] =  DB.withSession {
    implicit session =>
      (for (c <- seasons.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      seasons.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[Season] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = for {c <- seasons
          .sortBy(season => orderField match {
          case 1 => season.currentSeason.asc
          case -1 => season.currentSeason.desc
          case 2 => season.name.asc
          case -2 => season.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[Season] =  DB.withSession {
    implicit session =>
      seasons.where(_.id === id).firstOption
  }

  def insert(season: Season): Long =  DB.withSession {
    implicit session =>
      seasons.insert(season)
  }

  def update(id: Long, season: Season) =  DB.withSession {
    implicit session =>
      val season2update = season.copy(Some(id))
      seasons.where(_.id === id).update(season2update)
  }

  def delete(seasonId: Long) =  DB.withSession {
    implicit session =>
      seasons.where(_.id === seasonId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString(), c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- seasons
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val seasonFormat = Json.format[Season]

}



