package models


import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

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

  lazy val pageSize = 10

  def findAll: Seq[Club] = DB.withSession {
    implicit session => {
      (for (c <- Clubs.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      Query(Clubs.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Club] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
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

        Page(clubs, page, offset, count)
    }
  }

  def findById(id: Long): Option[Club] = DB.withSession {
    implicit session => {
      Clubs.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Club] = DB.withSession {
    implicit session => {
      Clubs.byName(name).firstOption
    }
  }

  def findByCode(code: Int): Option[Club] = DB.withSession {
    implicit session => {
      Clubs.byCode(code).firstOption
    }
  }

  def insert(club: Club): Long = DB.withSession {
    implicit session => {
      Clubs.autoInc.insert((club))
    }
  }

  def update(id: Long, club: Club) = DB.withSession {
    implicit session => {
      val club2update = club.copy(Some(id), club.code, club.name)
      Clubs.where(_.id === id).update(club2update)
    }
  }

  def delete(clubId: Long) = DB.withSession {
    implicit session => {
      Clubs.where(_.id === clubId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Clubs
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val clubFormat = Json.format[Club]

}

