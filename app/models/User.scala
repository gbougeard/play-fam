package models

import play.api.libs.Codecs

import models.Tokens._

import securesocial.core._

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
case class User(pid: Option[Long] = None,
                userId: String,
                providerId: String,
                email: Option[String],
                firstName: String,
                lastName: String,
                authMethod: AuthenticationMethod,
                oAuth1Info: Option[OAuth1Info] = None,
                oAuth2Info: Option[OAuth2Info] = None,
                passwordInfo: Option[PasswordInfo] = None,
                currentClubId: Option[Long] = None
                 ) extends Identity {
  def id: UserId = UserId(userId, providerId)

  def fullName: String = s"$firstName $lastName"

  def avatarUrl: Option[String] = email.map {
    e => s"http://www.gravatar.com/avatar/${Codecs.md5(e.getBytes)}.png"
  }
}

object User {
  def fromIdentity(user: Identity) = {
    User(pid = None,
      userId = user.id.id,
      providerId = user.id.providerId,
      email = user.email,
      firstName = user.firstName,
      lastName = user.lastName,
      authMethod = user.authMethod,
      oAuth1Info = user.oAuth1Info,
      oAuth2Info = user.oAuth2Info,
      passwordInfo = user.passwordInfo,
      currentClubId = None
    )
  }
}


object Users extends Table[User]("fam_user") {

  def oAuth1Info = {
    def token = column[String]("token")
    def secret = column[String]("secret")
    token ~ secret <>(OAuth1Info.apply _, OAuth1Info.unapply _)
  }

  def oAuth2Info = {
    def accessToken = column[String]("accessToken")
    def tokenType = column[Option[String]]("tokenType")
    def expiresIn = column[Option[Int]]("expiresIn")
    def refreshToken = column[Option[String]]("refreshToken")
    accessToken ~ tokenType ~ expiresIn ~ refreshToken <>(OAuth2Info.apply _, OAuth2Info.unapply _)
  }

  def passwordInfo = {
    def hasher = column[String]("hasher")
    def password = column[String]("password")
    def salt = column[Option[String]]("salt")
    hasher ~ password ~ salt <>(PasswordInfo.apply _, PasswordInfo.unapply _)
  }

  // Conversions for AuthenticationMethod
  implicit def string2AuthenticationMethod: TypeMapper[AuthenticationMethod] = MappedTypeMapper.base[AuthenticationMethod, String](
    authenticationMethod => authenticationMethod.method,
    string => AuthenticationMethod(string)
  )

  def pid = column[Long]("id_user", O.PrimaryKey, O.AutoInc)

  def email = column[String]("email", O.NotNull)

  def firstName = column[String]("first_name", O.NotNull)

  def lastName = column[String]("last_name", O.NotNull)

  def currentClubId = column[Long]("id_current_club")

  def userId = column[String]("user_id")

  def providerId = column[String]("provider_id")

  def password = column[String]("password")

  def authMethod = column[AuthenticationMethod]("authMethod")

  // Projections
  def * = {
    pid.? ~
      userId ~
      providerId ~
      email.? ~
      firstName ~
      lastName ~
      authMethod ~
      oAuth1Info.? ~
      oAuth2Info.? ~
      passwordInfo.? ~
      currentClubId.? <>(User.apply _, User.unapply _)
  }

  def autoInc = * returning pid

  // Operations
  def save(user: User): User = DB.withTransaction {
    implicit session =>
      user.pid match {
        case None | Some(0) => {
          val pid = this.autoInc.insert(user)
          user.copy(pid = Some(pid))
        }
        case Some(pid) => {
          Query(Users).where(_.pid is pid).update(user)
          user
        }
      }
  }

  def delete(pid: Long) = DB.withTransaction {
    implicit session =>
      this.where(_.pid is pid).mutate(_.delete)
  }

  // Queries
  def all: List[User] = DB.withSession {
    implicit session =>
      val q = for (user <- Users) yield user
      q.list
  }

  def findById(pid: Long): Option[User] = DB.withSession {
    implicit session =>
      def byId = createFinderBy(_.pid)
      byId(pid).firstOption
  }

  def findByEmail(email: String): Option[User] = DB.withSession {
    implicit session =>
      def byEmail = createFinderBy(_.email)
      byEmail(email).firstOption
  }

  def findByUserId(userId: UserId): Option[User] = DB.withSession {
    implicit session =>
      val q = for {
        user <- this if (this.userId is userId.id) && (this.providerId is userId.providerId)
      } yield user

      q.firstOption
  }

  def findByEmailAndProvider(email: String, providerId: String): Option[User] = DB.withSession {
    implicit session =>
      val q = for {
        user <- this if (this.email is email) && (this.providerId is providerId)
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

  implicit val authenticationMethodFormat = Json.format[AuthenticationMethod]
  implicit val oAuth1InfoFormat = Json.format[OAuth1Info]
  implicit val oAuth2InfoFormat = Json.format[OAuth2Info]
  implicit val passwordInfoFormat = Json.format[PasswordInfo]
  implicit val userFormat = Json.format[User]

}