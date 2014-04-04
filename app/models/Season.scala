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


  def findAll(implicit session: Session): Seq[Season] =  {
      (for (c <- seasons.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      (seasons.length).run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Season] = {

    val offset = pageSize * page

        val seasons = (
          for {c <- seasons
            .sortBy(season => orderField match {
            case 1 => season.currentSeason.asc
            case -1 => season.currentSeason.desc
            case 2 => season.name.asc
            case -2 => season.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(seasons, page, offset, count)
  }

  def findById(id: Long)(implicit session: Session): Option[Season] =  {
      seasons.where(_.id === id).firstOption
  }

  def insert(season: Season)(implicit session: Session): Long =  {
      seasons.insert(season)
  }

  def update(id: Long, season: Season)(implicit session: Session) =  {
      val season2update = season.copy(Some(id))
      seasons.where(_.id === id).update(season2update)
  }

  def delete(seasonId: Long)(implicit session: Session) =  {
      seasons.where(_.id === seasonId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString(), c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- seasons
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val seasonFormat = Json.format[Season]

}



