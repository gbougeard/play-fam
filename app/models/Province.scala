package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.States._

// Use the implicit threadLocalSession

import scala.slick.session.Database.threadLocalSession

case class Province(id: Long, code: String, name: String, upper: String, lower: String, stateId: Long)

// define tables
object Provinces extends Table[Province]("fam_province") {

  def id = column[Long]("id_province", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_province")

  def name = column[String]("lib_province")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def stateId = column[Long]("id_state")

  def * = id ~ code ~ name ~ upper ~ lower ~ stateId <>(Province, Province.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def state = foreignKey("STATE_FK", stateId, States)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byUpper = createFinderBy(_.upper)
  val byLower = createFinderBy(_.lower)
  val byState = createFinderBy(_.stateId)

  lazy val database = Database.forDataSource(DB.getDataSource())

  lazy val pageSize = 10

  def findAll: Seq[Province] = {
    database.withSession {
      (for (c <- Provinces.sortBy(_.code)) yield c).list
    }
  }

  def count: Int = {
    database.withSession {
      (for {c <- Provinces} yield c.id).list.size
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Province, State)] = {

    val offset = pageSize * page

    database.withSession {
      val provinces = (
        for {p <- Provinces
          .sortBy(_.code)
          .drop(offset)
          .take(pageSize)
             s <- p.state
        } yield (p, s)).list

      val totalRows = count
      Page(provinces, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[Province] = database withSession {
    Provinces.byId(id).firstOption
  }

  def findByName(name: String): Option[Province] = database withSession {
    Provinces.byName(name).firstOption
  }

  def findByCode(code: String): Option[Province] = database withSession {
    Provinces.byCode(code).firstOption
  }

  def insert(province: Province): Long = database withSession {
    Provinces.insert((province))
  }

  def update(province: Province) = database withSession {
    Provinces.where(_.id === province.id).update(province)
  }

  def delete(provinceId: Long) = database withSession {
    Provinces.where(_.id === provinceId).delete
  }

  def json(page: Int, pageSize: Int, orderField: Int): Seq[(Province, State)] = {

    database withSession {
      val provinces = for {p <- Provinces
        .sortBy(province => orderField match {
        case 1 => province.id.asc
        case -1 => province.id.desc
        case 2 => province.code.asc
        case -2 => province.code.desc
        case 3 => province.name.asc
        case -3 => province.name.desc
      })
        .drop(page)
        .take(pageSize)
       s <- p.state
      } yield (p, s)
      //      Json.toJson(cities.list)
      provinces.list
    }

  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.code + " - " + c.name)

  //JSON
  implicit val provinceFormat = Json.format[Province]

  implicit val provinceWithStateReads: Reads[(Province, State)] = (
    (__ \ 'province).read[Province] ~
      (__ \ 'state).read[State]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val provinceWithStateWrites: Writes[(Province, State)] = (
    (__ \ 'province).write[Province] ~
      (__ \ 'state).write[State]
    ) tupled
  implicit val provinceWithStateFormat = Format(provinceWithStateReads, provinceWithStateWrites)
}