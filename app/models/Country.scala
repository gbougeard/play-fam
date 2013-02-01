package models

import common.Profile

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Country(id: Option[Long],
                   code: String,
                   name: String,
                   upper: String,
                   lower: String)

trait CountryComponent {
  this: Profile =>

  import profile.simple._

  // define tables
  object Countries extends Table[Country]("fam_country") {

    def id = column[Long]("id_country", O.PrimaryKey, O.AutoInc)

    def code = column[String]("cod_country")

    def name = column[String]("lib_country")

    def upper = column[String]("lib_Upper")

    def lower = column[String]("lib_lower")

    def * = id.? ~ code ~ name ~ upper ~ lower <>(Country, Country.unapply _)
    def autoInc = id.? ~ code ~ name ~ upper ~ lower  <>(Country, Country.unapply _) returning id

    val byId = createFinderBy(_.id)
    val byName = createFinderBy(_.name)
    val byCode = createFinderBy(_.code)
    val byUpper = createFinderBy(_.upper)
    val byLower = createFinderBy(_.lower)

    lazy val pageSize = 10

    def findAll(implicit session: Session): Seq[Country] = {
      (for (c <- Countries.sortBy(_.name)) yield c).list
    }

    def count(implicit session: Session): Int = {
      (for {c <- Countries} yield c.id).list.size
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Country] = {

      val offset = pageSize * page

      val countrys = (
        for {t <- Countries
          .sortBy(_.id)
          .drop(offset)
          .take(pageSize)
        } yield t).list

      val totalRows = (for {t <- Countries} yield t.id).list.size
      Page(countrys, page, offset, totalRows)
    }

    def findById(id: Long)(implicit session: Session): Option[Country] = {
      Countries.byId(id).firstOption
    }

    def findByName(name: String)(implicit session: Session): Option[Country] = {
      Countries.byName(name).firstOption
    }

    def findByCode(code: String)(implicit session: Session): Option[Country] = {
      Countries.byCode(code).firstOption
    }

    def insert(country: Country)(implicit session: Session): Long = {
      Countries.autoInc.insert((country))
    }

    def update(country: Country)(implicit session: Session) = {
      Countries.where(_.id === country.id).update(country)
    }

    def delete(countryId: Long)(implicit session: Session) = {
      Countries.where(_.id === countryId).delete
    }

    def json(page: Int, pageSize: Int, orderField: Int)(implicit session: Session): Seq[Country] = {

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
    def options(implicit session: Session): Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

    //JSON
    implicit val countryFormat = Json.format[Country]

  }

}