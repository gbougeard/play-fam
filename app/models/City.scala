package models

import play.api.Play.current
import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


case class City(id: Option[Long],
                code: String,
                name: String,
                upper: String,
                lower: String,
                provinceId: Long)

object Cities extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[City] =  DB.withSession {
    implicit session =>
      (for (c <- cities.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      Query(cities.length).first
  }

  def findPage(page: Int = 0, orderField: Int): Page[(City, Province)] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = (
          for {c <- cities
               p <- c.province
          } yield (c, p))
        .sortBy(orderField match {
          case 1 => _._1.code.asc
          case -1 => _._1.code.desc
          case 2 => _._1.name.asc
          case -2 => _._1.name.desc
          case 3 => _._2.name.asc
          case -3 => _._2.name.desc
        })
          .drop(offset)
          .take(pageSize)

        val totalRows = count
        Page(q.list, page, offset, totalRows)
  }

  def findById(id: Long): Option[City] =  DB.withSession {
    implicit session =>
      cities.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[City] =  DB.withSession {
    implicit session =>
      cities.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[City] =  DB.withSession {
    implicit session =>
      cities.where(_.code === code).firstOption
  }

  def insert(city: City): Long =  DB.withSession {
    implicit session =>
      cities.insert(city)
  }

  def update(id: Long, city: City) =  DB.withSession {
    implicit session =>
      val city2update = city.copy(Some(id), city.code, city.name, city.upper, city.lower, city.provinceId)
      cities.where(_.id === id).update(city2update)
  }

  def delete(cityId: Long) =  DB.withSession {
    implicit session =>
      cities.where(_.id === cityId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- cities
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }


  def json(page: Int, pageSize: Int, orderField: Int): Seq[(City, Province)] =  DB.withSession {
    implicit session =>

      play.Logger.debug("page " + page)
      play.Logger.debug("pageSize " + pageSize)
      play.Logger.debug("orderField " + orderField)

      val q = for {c <- cities
        .sortBy(city => orderField match {
        case 1 => city.id.asc
        case -1 => city.id.desc
        case 2 => city.code.asc
        case -2 => city.code.desc
        case 3 => city.name.asc
        case -3 => city.name.desc
      })
        .drop(page)
        .take(pageSize)
                        p <- c.province
      } yield (c, p)
      //      Json.toJson(cities.list)
      q.list

  }

}