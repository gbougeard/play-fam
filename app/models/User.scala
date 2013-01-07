package models

import common.Profile

/**
 * Created with IntelliJ IDEA.
 * User: gonto
 * Date: 11/23/12
 * Time: 9:47 PM
 * To change this template use File | Settings | File Templates.
 */
case class User(id: Option[Long],
                email: String)

trait UserComponent {
  this: Profile =>

  import profile.simple._

  object Users extends Table[User]("fam_user") {
    def id = column[Long]("id_user", O.PrimaryKey)

    def email = column[String]("email", O.NotNull)

    def * = id.? ~ email <>(User, User.unapply _)

    def add(user: User)(implicit session: Session) = {
      this.insert(user)
    }

    def countByEmail(email: String)(implicit session: Session) = {
      (for {
        user <- Users
        if (user.email === email)
      } yield (user)).list.size
    }

    def findAll(implicit session: Session) = {
      (for {
        user <- Users
      } yield (user)).list
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[User] = {
      val pageSize = 10
      val offset = pageSize * page

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