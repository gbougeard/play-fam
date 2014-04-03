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

object Season{
  lazy val pageSize = 10


  def findAll: Seq[Season] =  {
    implicit session:Session => {
      (for (c <- Seasons.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(Seasons.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Season] = {

    val offset = pageSize * page

     {
      implicit session:Session => {
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

        Page(seasons, page, offset, count)
      }
    }
  }

  def findById(id: Long): Option[Season] =  {
    implicit session:Session => {
      Seasons.byId(id).firstOption
    }
  }

  def insert(season: Season): Long =  {
    implicit session:Session => {
      Seasons.autoInc.insert(season)
    }
  }

  def update(id: Long, season: Season) =  {
    implicit session:Session => {
      val season2update = season.copy(Some(id))
      Seasons.where(_.id === id).update(season2update)
    }
  }

  def delete(seasonId: Long) =  {
    implicit session:Session => {
      Seasons.where(_.id === seasonId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString(), c.name)
  def options: Seq[(String, String)] =  {
    implicit session:Session =>
      val query = (for {
        item <- Seasons
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val seasonFormat = Json.format[Season]

}



