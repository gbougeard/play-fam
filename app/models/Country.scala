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
object Country{
  lazy val pageSize = 10

  lazy val countryCount = count

  def findAll: Seq[Country] =  {
    implicit session:Session => {
      (for (c <- Countries.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(Countries.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Country] = {

    val offset = pageSize * page
     {
      implicit session:Session => {
        val countrys = for {t <- Countries
          .sortBy(_.id)
          .drop(offset)
          .take(pageSize)
        } yield t

        Page(countrys.list, page, offset, countryCount)
      }
    }
  }

  def findById(id: Long): Option[Country] =  {
    implicit session:Session => {
      Countries.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Country] =  {
    implicit session:Session => {
      Countries.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Country] =  {
    implicit session:Session => {
      Countries.byCode(code).firstOption
    }
  }

  def insert(country: Country): Long =  {
    implicit session:Session => {
      Countries.autoInc.insert(country)
    }
  }

  def update(id: Long, country: Country) =  {
    implicit session:Session => {
      val country2update = country.copy(Some(id), country.code, country.name, country.upper, country.lower)
      Countries.where(_.id === id).update(country2update)
    }
  }

  def delete(countryId: Long) =  {
    implicit session:Session => {
      Countries.where(_.id === countryId).delete
    }
  }

  def json(page: Int, pageSize: Int, orderField: Int): Seq[Country] =  {
    implicit session:Session => {

      val countries = for {c <- Countries
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
      countries.list
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  {
    implicit session:Session =>
      val query = (for {
        item <- Countries
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  //JSON
  implicit val countryFormat = Json.format[Country]

}