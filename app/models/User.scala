package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import securesocial.core._

import play.Logger
import database.Users

case class User(pid: Option[Long],
                userId: String,
                providerId: String,
                email: Option[String],
                firstName: Option[String],
                lastName: Option[String],
                authMethod: AuthenticationMethod,
                hasher: Option[String],
                password: Option[String],
                salt: Option[String],
                currentClubId: Option[Long],
                avatarUrl: Option[String]
                 ) {
  def id: IdentityId = IdentityId(userId, providerId)

  def fullName: String = s"${firstName.getOrElse("")} ${lastName.getOrElse("")}"

//  def avatarUrl: Option[String] = email.map {
//    e => s"http://www.gravatar.com/avatar/${Codecs.md5(e.getBytes)}.png"
//  }
}


object User{
  // Operations
  def save(user: User): User = DB.withTransaction {
    implicit session:Session =>
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
          val pid = Users.autoInc.insert(user)
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
    implicit session:Session =>
      Logger.info("delete %s".format(pid))
      Users.where(_.pid is pid).mutate(_.delete)
  }

  // Queries
  def all: List[User] =  {
    implicit session:Session =>
      Logger.info("all")
      val q = for (user <- Users) yield user
      q.list
  }

  def findById(pid: Long): Option[User] =  {
    implicit session:Session =>
      Logger.info("findById %s".format(pid))
      def byId = Users.createFinderBy(_.pid)
      byId(pid).firstOption
  }

  //  def findByEmail(email: String): Option[User] =  {
  //    implicit session:Session =>
  //      def byEmail = createFinderBy(_.email)
  //      byEmail(email).firstOption
  //  }

  def findByUserId(u: IdentityId): Option[User] =  {
    implicit session:Session =>
      Logger.info("findByUserId %s".format(u))
      val q = for {
        user <- Users
        if (user.userId is u.userId) && (user.providerId is u.providerId)
      } yield user

      q.firstOption
  }

  def findByEmailAndProvider(e: String, p: String): Option[User] =  {
    implicit session:Session =>
      Logger.info("findByEmailAndProvider %s %s".format(e, p))
      val q = for {
        user <- Users
        if (user.email is e)  && (user.providerId is p)
      } yield user

      q.firstOption
  }

  def count: Int =  {
    implicit session:Session => {
      Query(Users.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[User] = {
    val pageSize = 10
    val offset = pageSize * page

     {
      implicit session:Session => {

        val users = (
          for {t <- Users
            .sortBy(user => orderField match {
            case 1 => user.email.asc
            case -1 => user.email.desc
            case 2 => user.lastName.asc
            case -2 => user.lastName.desc
            case 3 => user.providerId.asc
            case -3 => user.providerId.desc
            case _ => user.email.asc
          })
            .drop(offset)
            .take(pageSize)
          } yield t).list

        Page(users, page, offset, count)
      }
    }
  }

  //  def options: Seq[(String, String)] =  {
  //    implicit session:Session =>
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


  def fromIdentity(user: Identity) = {
    Logger.debug("fromIdentity %s".format(user))
    User(
      pid = None,
      userId = user.identityId.userId,
      providerId = user.identityId.providerId,
      email = user.email,
      firstName = if (!user.firstName.isEmpty) Some(user.firstName) else Some(user.fullName.split(' ').head),
      lastName = if (!user.lastName.isEmpty) Some(user.lastName) else Some(user.fullName.split(' ').tail.head),
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