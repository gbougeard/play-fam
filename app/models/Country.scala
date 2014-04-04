package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._
import database.Countries


case class Country(id: Option[Long],
                   code: String,
                   name: String,
                   upper: String,
                   lower: String)
object Countries extends DAO{
  lazy val pageSize = 10

  lazy val countryCount = count

  def findAll(implicit session: Session): Seq[Country] =  {
      (for (c <- countries.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      countries.length.run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Country] = {

    val offset = pageSize * page
        val countrys = for {t <- countries
          .sortBy(_.id)
          .drop(offset)
          .take(pageSize)
        } yield t

        Page(countrys.list, page, offset, countryCount)
  }

  def findById(id: Long)(implicit session: Session): Option[Country] =  {
      countries.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[Country] =  {
      countries.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[Country] =  {
      countries.where(_.code === code).firstOption
  }

  def insert(country: Country)(implicit session: Session): Long =  {
      countries.insert(country)
  }

  def update(id: Long, country: Country)(implicit session: Session) =  {
      val country2update = country.copy(Some(id), country.code, country.name, country.upper, country.lower)
      countries.where(_.id === id).update(country2update)
  }

  def delete(countryId: Long)(implicit session: Session) =  {
      countries.where(_.id === countryId).delete
  }

  def json(page: Int, pageSize: Int, orderField: Int)(implicit session: Session): Seq[Country] =  {

      val q = for {c <- countries
        .sortBy(country => orderField match {
        case 1 => country.id.asc
        case -1 => country.id.desc
        case 2 => country.code.asc
        case -2 => country.code.desc
        case 3 => country.name.asc
        case -3 => country.name.desc
      })
        .drop(page)
        .take(pageSize)
      } yield c
      //      Json.toJson(cities.list)
      q.list
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- countries
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  //JSON
  implicit val countryFormat = Json.format[Country]

}