package models.database

import play.api.db.slick.Config.driver.simple._
import securesocial.core.{OAuth1Info, OAuth2Info, IdentityId, AuthenticationMethod}
import models.{Category, User}
import scala.slick.lifted.Tag

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:54
 * To change this template use File | Settings | File Templates.
 */
  class Users(tag:Tag) extends Table[User](tag, "fam_user") {

  // Conversions for AuthenticationMethod
//  implicit def string2AuthenticationMethod: TypeMapper[AuthenticationMethod] = MappedTypeMapper.base[AuthenticationMethod, String](
//    authenticationMethod => authenticationMethod.method,
//    string => AuthenticationMethod(string)
//  )

  implicit def string2AuthenticationMethod = MappedColumnType.base[AuthenticationMethod, String](
    authenticationMethod => authenticationMethod.method,
    string => AuthenticationMethod(string)
  )

  implicit def tuple2OAuth1Info(tuple: (Option[String], Option[String])): Option[OAuth1Info] = tuple match {
    case (Some(token), Some(secret)) => Some(OAuth1Info(token, secret))
    case _ => None
  }

  implicit def tuple2OAuth2Info(tuple: (Option[String], Option[String], Option[Int], Option[String])): Option[OAuth2Info] = tuple match {
    case (Some(token), tokenType, expiresIn, refreshToken) => Some(OAuth2Info(token, tokenType, expiresIn, refreshToken))
    case _ => None
  }

  implicit def tuple2IdentityId(tuple: (String, String)): IdentityId = tuple match {
    case (userId, providerId) => IdentityId(userId, providerId)
  }

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
  //    token , secret <> (OAuth1Info.apply _, OAuth1Info.unapply _)
  //  }

  //  def oAuth2Info = {
  //    def accessToken = column[String]("accessToken")
  //    def tokenType = column[Option[String]]("tokenType")
  //    def expiresIn = column[Option[Int]]("expiresIn")
  //    def refreshToken = column[Option[String]]("refreshToken")
  //    accessToken , tokenType , expiresIn , refreshToken <> (OAuth2Info.apply _, OAuth2Info.unapply _)
  //  }

  //    def passwordInfo = {
  def hasher = column[String]("hasher")

  def password = column[String]("password")

  def salt = column[String]("salt")

  def currentClubId = column[Long]("id_current_club")

  def avatarUrl = column[String]("avatarUrl")

  //      def apply = hasher , password , salt.? <> (PasswordInfo.apply _, PasswordInfo.unapply _)
  //    }

  // Projections
  //  def * =  pid.? , userId , providerId , email.? , firstName , lastName ,  authMethod , passwordInfo.? <>(User.apply _, User.unapply _)
  def * = (pid.? , userId , providerId , email.? , firstName.? , lastName.? , authMethod , hasher.? , password.? , salt.? , currentClubId.? , avatarUrl.? )<>(User.tupled, User.unapply _)



}
