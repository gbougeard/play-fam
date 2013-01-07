package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Countries._

// Use the implicit threadLocalSession

import scala.slick.session.Database.threadLocalSession

case class State(id: Long, code: String, name: String, upper: String, lower: String, countryId : Long)

// define tables
object States extends Table[State]("fam_state") {

  def id = column[Long]("id_state", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_state")

  def name = column[String]("lib_state")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def countryId = column[Long]("id_country")

  def * = id ~ code ~ name ~ upper ~ lower ~ countryId <>(State, State.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def country = foreignKey("COUNTRY_FK", countryId, Countries)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byUpper = createFinderBy(_.upper)
  val byLower = createFinderBy(_.lower)
  val byCountry = createFinderBy(_.countryId)

  lazy val database = Database.forDataSource(DB.getDataSource())

  lazy val pageSize = 10

  def findAll: Seq[State] = {
    database.withSession {
      (for (c <- States.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = {
    database.withSession {
      (for {c <- States} yield c.id).list.size
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(State, Country)] = {

    val offset = pageSize * page

    database.withSession {
      val states = (
        for {s <- States
          .sortBy(_.lower)
          .drop(offset)
          .take(pageSize)
          c <- s.country
        } yield (s, c)).list

      val totalRows = count
      Page(states, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[State] = database withSession {
    States.byId(id).firstOption
  }

  def findByName(name: String): Option[State] = database withSession {
    States.byName(name).firstOption
  }

  def findByCode(code: String): Option[State] = database withSession {
    States.byCode(code).firstOption
  }

  def insert(state: State): Long = database withSession {
    States.insert((state))
  }

  def update(state: State) = database withSession {
    States.where(_.id === state.id).update(state)
  }

  def delete(stateId: Long) = database withSession {
    States.where(_.id === stateId).delete
  }

  def json(page: Int, pageSize: Int, orderField: Int): Seq[(State, Country)] = {

    database withSession {
      val states = for {s <- States
        .sortBy(state => orderField match {
        case 1 => state.id.asc
        case -1 => state.id.desc
        case 2 => state.code.asc
        case -2 => state.code.desc
        case 3 => state.name.asc
        case -3 => state.name.desc
      })
        .drop(page)
        .take(pageSize)
        c <- s.country
      } yield (s, c)
      //      Json.toJson(cities.list)
      states.list
    }

  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  //JSON
  implicit val stateFormat = Json.format[State]

  implicit val stateWithCountryReads: Reads[(State, Country)] = (
    (__ \ 'state).read[State] ~
      (__ \ 'country).read[Country]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val stateWithCountryWrites: Writes[(State, Country)] = (
    (__ \ 'state).write[State] ~
      (__ \ 'country).write[Country]
    ) tupled
  implicit val stateWithCountryFormat = Format(stateWithCountryReads, stateWithCountryWrites)
}