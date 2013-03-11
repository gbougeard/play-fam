package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Country(id: Option[Long],
                   code: String,
                   name: String,
                   upper: String,
                   lower: String)

// define tables
object Countries extends Table[Country]("fam_country") {

  def id = column[Long]("id_country", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_country")

  def name = column[String]("lib_country")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def * = id.? ~ code ~ name ~ upper ~ lower <>(Country, Country.unapply _)

  def autoInc = id.? ~ code ~ name ~ upper ~ lower <>(Country, Country.unapply _) returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byUpper = createFinderBy(_.upper)
  val byLower = createFinderBy(_.lower)

  lazy val pageSize = 10

  def findAll: Seq[Country] = DB.withSession {
    implicit session => {
      (for (c <- Countries.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      (for {c <- Countries} yield c.id).list.size
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Country] = {

    val offset = pageSize * page
    DB.withSession {
      implicit session => {
        val countrys = (
          for {t <- Countries
            .sortBy(_.id)
            .drop(offset)
            .take(pageSize)
          } yield t).list

        val totalRows = (for {t <- Countries} yield t.id).list.size
        Page(countrys, page, offset, totalRows)
      }
    }
  }

  def findById(id: Long): Option[Country] = DB.withSession {
    implicit session => {
      Countries.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Country] = DB.withSession {
    implicit session => {
      Countries.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Country] = DB.withSession {
    implicit session => {
      Countries.byCode(code).firstOption
    }
  }

  def insert(country: Country): Long = DB.withSession {
    implicit session => {
      Countries.autoInc.insert((country))
    }
  }

  def update(id: Long, country: Country) = DB.withSession {
    implicit session => {
      val country2update = country.copy(Some(id), country.code, country.name, country.upper, country.lower)
      Countries.where(_.id === id).update(country2update)
    }
  }

  def delete(countryId: Long) = DB.withSession {
    implicit session => {
      Countries.where(_.id === countryId).delete
    }
  }

  def json(page: Int, pageSize: Int, orderField: Int): Seq[Country] = DB.withSession {
    implicit session => {

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
  def options: Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Countries
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  //JSON
  implicit val countryFormat = Json.format[Country]

}