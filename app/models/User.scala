package models

import play.api.Play.current
import play.api.Logger

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.libs.json.Json

/**
 * Created with IntelliJ IDEA.
 * User: gonto
 * Date: 11/23/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
case class User(id: Option[Long],
                email: String,
                firstName: String,
                lastName: String,
                currentClubId: Option[Long],
                oauthProvider: Option[String],
                oauthId: Option[String],
                password: Option[String])


object Users extends Table[User]("fam_user") {
  def id = column[Long]("id_user", O.PrimaryKey, O.AutoInc)

  def email = column[String]("email", O.NotNull)

  def firstName = column[String]("first_name", O.NotNull)

  def lastName = column[String]("last_name", O.NotNull)

  def currentClubId = column[Long]("id_current_club")

  def oauthProvider = column[String]("oauth_provider")

  def oauthId = column[String]("oauth_id")
  def password = column[String]("password")

  def * = id.? ~ email ~ firstName ~ lastName ~ currentClubId.? ~ oauthProvider.? ~ oauthId.? ~ password.? <>(User, User.unapply _)

  def autoInc = id.? ~ email ~ firstName ~ lastName ~ currentClubId.? ~ oauthProvider.? ~ oauthId.? ~ password.? <>(User, User.unapply _) returning id

  val byId = createFinderBy(_.id)


  def add(user: User) = DB.withSession {
    implicit session => {
      this.insert(user)
    }
  }

  def findById(id: Long): Option[User] = DB.withSession {
    implicit session => {
      Users.byId(id).firstOption
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

  def findByOauth(provider: String, inId: String): Option[User] = DB.withSession {
    implicit session => {
      Logger.info("finding oauth user for %s %s".format(provider, inId))
      (for {
        user <- Users
        if (user.oauthProvider === provider)
        if (user.oauthId === inId)
      } yield (user)).firstOption
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

  def insert(user: User): Long = DB.withSession {
    implicit session => {
      Users.autoInc.insert((user))
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

  implicit val userFormat = Json.format[User]

}