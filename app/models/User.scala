package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._


// Use the implicit threadLocalSession

import scala.slick.session.Database.threadLocalSession

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

  lazy val database = Database.forDataSource(DB.getDataSource())

  def add(user: User) = database withSession {
    this.insert(user)
  }

  def countByEmail(email: String) = database withSession {
    (for {
      user <- Users
      if (user.email === email)
    } yield (user)).list.size
  }

  def findAll = database withSession {
    (for {
      user <- Users
    } yield (user)).list
  }

  def findPage(page: Int = 0, orderField: Int): Page[User] = {
    val pageSize = 10
    val offset = pageSize * page

    database withSession {

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