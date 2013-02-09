package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._


// Use the implicit threadLocalSession

import scala.slick.session.Database.threadLocalSession

case class Club(id: Option[Long],
                code: Int,
                name: String)


// define tables
object Clubs extends Table[Club]("fam_club") {

  def id = column[Long]("id_club", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_club")

  def code = column[Int]("code_fff")

  def * = id.? ~ code ~ name <>(Club, Club.unapply _)

  def autoInc = id.? ~ code ~ name <>(Club, Club.unapply _) returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val pageSize = 10

  def findAll: Seq[Club] = {
    (for (c <- Clubs.sortBy(_.name)) yield c).list
  }

  def findPage(page: Int = 0, orderField: Int): Page[Club] = {

    val offset = pageSize * page

    database withSession {
      val clubs = (
        for {c <- Clubs
          .sortBy(club => orderField match {
          case 1 => club.code.asc
          case -1 => club.code.desc
          case 2 => club.name.asc
          case -2 => club.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c).list

      val totalRows = (for (c <- Clubs) yield c.id).list.size
      Page(clubs, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[Club] = database withSession {
    Clubs.byId(id).firstOption
  }

  def findByName(name: String): Option[Club] = database withSession {
    Clubs.byName(name).firstOption
  }

  def findByCode(code: Int): Option[Club] = database withSession {
    Clubs.byCode(code).firstOption
  }

  def insert(club: Club): Long = database withSession {
    Clubs.autoInc.insert((club))
  }

  def update(id: Long, club: Club) = database withSession {
    val club2update = club.copy(Some(id), club.code, club.name)
    Clubs.where(_.id === id).update(club2update)
  }

  def delete(clubId: Long) = database withSession {
    Clubs.where(_.id === clubId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  implicit val clubFormat = Json.format[Club]

}

