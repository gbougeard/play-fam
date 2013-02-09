package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._


// Use the implicit threadLocalSession

import scala.slick.session.Database.threadLocalSession

case class City(id: Option[Long],
                code: String,
                name: String,
                upper: String,
                lower: String,
                provinceId: Long)

// define tables
object Cities extends Table[City]("fam_city") {

  def id = column[Long]("id_city", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_city")

  def name = column[String]("lib_city")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def provinceId = column[Long]("id_province")

  def * = id.? ~ code ~ name ~ upper ~ lower ~ provinceId <>(City, City.unapply _)

  def autoInc = id.? ~ code ~ name ~ upper ~ lower ~ provinceId <>(City, City.unapply _) returning id


  // A reified foreign key relation that can be navigated to create a join
  def province = foreignKey("PROVINCE_FK", provinceId, Provinces)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byUpper = createFinderBy(_.upper)
  val byLower = createFinderBy(_.lower)
  val byProvince = createFinderBy(_.provinceId)

  lazy val pageSize = 10
  lazy val database = Database.forDataSource(DB.getDataSource())

  def findAll: Seq[City] = {
    (for (c <- Cities.sortBy(_.name)) yield c).list
  }

  def count: Int = {
    (for {c <- Cities} yield c.id).list.size
  }

  def findPage(page: Int = 0, orderField: Int): Page[(City, Province)] = {

    val offset = pageSize * page

    database withSession {
      val cities = (
        for {c <- Cities
          .sortBy(_.code)
          .drop(offset)
          .take(pageSize)
             p <- c.province
        } yield (c, p)).list

      val totalRows = count
      Page(cities, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[City] = database withSession {
    Cities.byId(id).firstOption
  }

  def findByName(name: String): Option[City] = database withSession{
    Cities.byName(name).firstOption
  }

  def findByCode(code: String): Option[City] =database withSession {
    Cities.byCode(code).firstOption
  }

  def insert(city: City): Long = database withSession{
    Cities.autoInc.insert((city))
  }

  def update(id:Long,city: City) = database withSession{
    val city2update = city.copy(Some(id), city.code, city.name, city.upper, city.lower, city.provinceId)
    Cities.where(_.id === id).update(city2update)
  }

  def delete(cityId: Long) = database withSession{
    Cities.where(_.id === cityId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)


  def json(page: Int, pageSize: Int, orderField: Int): Seq[(City, Province)] = {

    println("page " + page)
    println("pageSize " + pageSize)
    println("orderField " + orderField)

    val cities = for {c <- Cities
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
    cities.list

  }

}