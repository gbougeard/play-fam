package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

/**
 * Created with IntelliJ IDEA.
 * User: gonto
 * Date: 11/23/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
case class User(id: Option[Long],
                email: String)

object Users extends Table[User]("fam_user") {
  def id = column[Long]("id_user", O.PrimaryKey)

  def email = column[String]("email", O.NotNull)

  def * = id.? ~ email <>(User, User.unapply _)

  def add(user: User) = DB.withSession {
    implicit session => {
      this.insert(user)
    }
  }

  def countByEmail(email: String) = DB.withSession {
    implicit session => {
      (for {
        user <- Users
        if (user.email === email)
      } yield (user)).list.size
    }
  }

  def findAll = DB.withSession {
    implicit session => {
      (for {
        user <- Users
      } yield (user)).list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[User] = {
    val pageSize = 10
    val offset = pageSize * page

    DB.withSession {
      implicit session => {

        val users = (
          for {t <- Users
            .sortBy(user => orderField match {
            case 1 => user.id.asc
            case -1 => user.id.desc
            case 2 => user.email.asc
            case -2 => user.email.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield (t)).list

        val totalRows = (for {t <- Users} yield t.id).list.size
        Page(users, page, offset, totalRows)
      }
    }
  }

  def options: Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Users
      } yield (item.id, item.email)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}