package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import securesocial.core._

import play.Logger

case class User(pid: Option[Long],
                userId: String,
                providerId: String,
                email: Option[String],
                firstName: String,
                lastName: String,
                authMethod: AuthenticationMethod,
                hasher: Option[String],
                password: Option[String],
                salt: Option[String],
                currentClubId: Option[Long],
                avatarUrl: Option[String]
                 ) {
  def id: UserId = UserId(userId, providerId)

  def fullName: String = s"$firstName $lastName"

//  def avatarUrl: Option[String] = email.map {
//    e => s"http://www.gravatar.com/avatar/${Codecs.md5(e.getBytes)}.png"
//  }
}


object Users extends Table[User]("fam_user") {

  // Conversions for AuthenticationMethod
  implicit def string2AuthenticationMethod: TypeMapper[AuthenticationMethod] = MappedTypeMapper.base[AuthenticationMethod, String](
    authenticationMethod => authenticationMethod.method,
    string => AuthenticationMethod(string)
  )

  def pid = column[Long]("id_user", O.PrimaryKey, O.AutoInc)

  def userId = column[String]("userId")

  def providerId = column[String]("providerId")

  def email = column[String]("email")

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def authMethod = column[AuthenticationMethod]("authMethod")

  //  def oAuth1Info = {
  //    def token = column[String]("token")
  //    def secret = column[String]("secret")
  //    token ~ secret <> (OAuth1Info.apply _, OAuth1Info.unapply _)
  //  }

  //  def oAuth2Info = {
  //    def accessToken = column[String]("accessToken")
  //    def tokenType = column[Option[String]]("tokenType")
  //    def expiresIn = column[Option[Int]]("expiresIn")
  //    def refreshToken = column[Option[String]]("refreshToken")
  //    accessToken ~ tokenType ~ expiresIn ~ refreshToken <> (OAuth2Info.apply _, OAuth2Info.unapply _)
  //  }

  //    def passwordInfo = {
  def hasher = column[String]("hasher")

  def password = column[String]("password")

  def salt = column[String]("salt")

  def currentClubId = column[Long]("id_current_club")

  def avatarUrl = column[String]("avatarUrl")

  //      def apply = hasher ~ password ~ salt.? <> (PasswordInfo.apply _, PasswordInfo.unapply _)
  //    }

  // Projections
  //  def * =  pid.? ~ userId ~ providerId ~ email.? ~ firstName ~ lastName ~  authMethod ~ passwordInfo.? <>(User.apply _, User.unapply _)
  def * = pid.? ~ userId ~ providerId ~ email.? ~ firstName ~ lastName ~ authMethod ~ hasher.? ~ password.? ~ salt.? ~ currentClubId.? ~ avatarUrl.? <>(User.apply _, User.unapply _)

  def autoInc = * returning pid

  // Operations
  def save(user: User): User = DB.withTransaction {
    implicit session =>
      Logger.info("save %s".format(user))
//      val u = findByUserId(user.id).match{
//         x => Query(Users).where(_.pid is user.pid).update(user)
//          user
//      } getOrElse(
//
//        )
//      u

//      Logger.info("found user %s".format(u))
      val x = findByUserId(user.id)
      Logger.info("found %s".format(x))
      findByUserId(user.id) match {
        case None  => {
          Logger.info("create user")
          val pid = this.autoInc.insert(user)
          user.copy(pid = Some(pid))
        }
        case Some(u) => {
          Logger.info("update user %s".format(user.email))
          Query(Users).where(_.email is u.email).update(user.copy(pid=u.pid, currentClubId=u.currentClubId))
          u
        }
      }
  }

  def delete(pid: Long) = DB.withTransaction {
    implicit session =>
      Logger.info("delete %s".format(pid))
      this.where(_.pid is pid).mutate(_.delete)
  }

  // Queries
  def all: List[User] = DB.withSession {
    implicit session =>
      Logger.info("all")
      val q = for (user <- Users) yield user
      q.list
  }

  def findById(pid: Long): Option[User] = DB.withSession {
    implicit session =>
      Logger.info("findById %s".format(pid))
      def byId = createFinderBy(_.pid)
      byId(pid).firstOption
  }

  //  def findByEmail(email: String): Option[User] = DB.withSession {
  //    implicit session =>
  //      def byEmail = createFinderBy(_.email)
  //      byEmail(email).firstOption
  //  }

  def findByUserId(u: UserId): Option[User] = DB.withSession {
    implicit session =>
      Logger.info("findByUserId %s".format(u))
      val q = for {
        user <- Users
        if (user.userId is u.id) && (user.providerId is u.providerId)
      } yield user

      q.firstOption
  }

  def findByEmailAndProvider(e: String, p: String): Option[User] = DB.withSession {
    implicit session =>
      Logger.info("findByEmailAndProvider %s %s".format(e, p))
      val q = for {
        user <- Users
        if (user.email is e)  && (user.providerId is p)
      } yield user

      q.firstOption
  }

  def findPage(page: Int = 0, orderField: Int): Page[User] = {
    val pageSize = 10
    val offset = pageSize * page

    DB.withSession {
      implicit session => {

        val users = (
          for {t <- Users
            .sortBy(user => orderField match {
            case 1 => user.pid.asc
            case -1 => user.pid.desc
            case 2 => user.email.asc
            case -2 => user.email.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield (t)).list

        val totalRows = (for {t <- Users} yield t.pid).list.size
        Page(users, page, offset, totalRows)
      }
    }
  }

  //  def options: Seq[(String, String)] = DB.withSession {
  //    implicit session =>
  //      val query = (for {
  //        item <- Users
  //      } yield (item.id, item.email)
  //        ).sortBy(_._2)
  //      query.list.map(row => (row._1.toString, row._2))
  //  }

  //  implicit val authenticationMethodFormat = Json.format[AuthenticationMethod]
  //  implicit val oAuth1InfoFormat = Json.format[OAuth1Info]
  //  implicit val oAuth2InfoFormat = Json.format[OAuth2Info]
  //  implicit val passwordInfoFormat = Json.format[PasswordInfo]
  //  implicit val userFormat = Json.format[User]

}

object User {
  def fromIdentity(user: Identity) = {
    Logger.debug("fromIdentity %s".format(user))
    User(
      pid = None,
      userId = user.id.id,
      providerId = user.id.providerId,
      email = user.email,
      firstName = if (!user.firstName.isEmpty) user.firstName else user.fullName.split(' ').head,
      lastName = if (!user.lastName.isEmpty) user.lastName else user.fullName.split(' ').tail.head,
      authMethod = user.authMethod,
      hasher = user.passwordInfo.map {
        p => p.hasher
      },
      password = user.passwordInfo.map {
        p => p.password
      },
      salt = user.passwordInfo.map {
        p => p.salt
      }.getOrElse(None),
      currentClubId = None,
      avatarUrl = user.avatarUrl
    )
  }
}