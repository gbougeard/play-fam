package models


import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


case class Club(id: Option[Long] = None,
                code: Int,
                name: String,
                countryId: Option[Long] = None,
                cityId: Option[Long] = None,
                colours: Option[String] = None,
                address: Option[String] = None,
                zipcode: Option[String] = None,
                city: Option[String] = None,
                organization: Option[Long] = None,
                comments: Option[String] = None
                 )

object Clubs extends DAO {

  lazy val pageSize = 10

  def findAll: Seq[Club] =  DB.withSession {
    implicit session =>
      (for (c <- clubs.sortBy(_.name)) yield c).list
  }

  def count(filter: String): Int =  DB.withSession {
    implicit session =>
      clubs.where(_.name like s"%$filter%").length.run
  }

  def findPage(page: Int = 0, orderField: Int, filter: String): Page[Club] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = (
          for {c <- clubs
               if c.name like s"%$filter%"
          } yield c)
          .sortBy(club => orderField match {
          case 1 => club.code.asc
          case -1 => club.code.desc
          case 2 => club.name.asc
          case -2 => club.name.desc
          case 3 => club.city.asc
          case -3 => club.city.desc
          case 4 => club.zipcode.asc
          case -4 => club.zipcode.desc
        })
          .drop(offset)
          .take(pageSize)

        Page(q.list, page, offset, count(filter))
  }

  def findById(id: Long): Option[Club] =  DB.withSession {
    implicit session =>
      clubs.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[Club] =  DB.withSession {
    implicit session =>
      clubs.where(_.name === name).firstOption
  }

  def findByCode(code: Int): Option[Club] =  DB.withSession {
    implicit session =>
      clubs.where(_.code === code).firstOption
  }

  def findByZipCode(code: String): Seq[Club] =  DB.withSession {
    implicit session =>
      clubs.where(_.zipcode === code).list
  }

  def findByCity(city: String): Seq[Club] =  DB.withSession {
    implicit session =>
      clubs.where(_.city === city).list
  }

  def findLikeCity(c: String): Seq[Club] =  DB.withSession {
    implicit session =>
      clubs.where(_.city like s"%$c%").list
  }

  def insert(club: Club): Long =  DB.withSession {
    implicit session =>
      clubs.insert(club)
  }

  def update(id: Long, club: Club) =  DB.withSession {
    implicit session =>
      val club2update = club.copy(Some(id), club.code, club.name)
      clubs.where(_.id === id).update(club2update)
  }

  def delete(clubId: Long) =  DB.withSession {
    implicit session =>
      clubs.where(_.id === clubId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {
  //    c <- findAll
  //  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- clubs
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}

