package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._


// Use the implicit threadLocalSession

import scala.slick.session.Database.threadLocalSession

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

  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val pageSize = 10

  def findAll: Seq[Country] = {
    (for (c <- Countries.sortBy(_.name)) yield c).list
  }

  def count: Int = {
    (for {c <- Countries} yield c.id).list.size
  }

  def findPage(page: Int = 0, orderField: Int): Page[Country] = {

    val offset = pageSize * page
    database withSession {
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

  def findById(id: Long): Option[Country] = database withSession {
    Countries.byId(id).firstOption
  }

  def findByName(name: String): Option[Country] = database withSession {
    Countries.byName(name).firstOption
  }

  def findByCode(code: String): Option[Country] = database withSession {
    Countries.byCode(code).firstOption
  }

  def insert(country: Country): Long = database withSession {
    Countries.autoInc.insert((country))
  }

  def update(id: Long, country: Country) = database withSession {
    val country2update = country.copy(Some(id), country.code, country.name, country.upper, country.lower)
    Countries.where(_.id === id).update(country2update)
  }

  def delete(countryId: Long) = database withSession {
    Countries.where(_.id === countryId).delete
  }

  def json(page: Int, pageSize: Int, orderField: Int): Seq[Country] = database withSession {

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

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  //JSON
  implicit val countryFormat = Json.format[Country]


}