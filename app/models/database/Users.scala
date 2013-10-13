package models.database

import play.api.db.slick.Config.driver.simple._
import securesocial.core.AuthenticationMethod
import models.User

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:54
 * To change this template use File | Settings | File Templates.
 */
private[models] object Users extends Table[User]("fam_user") {

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
  def * = pid.? ~ userId ~ providerId ~ email.? ~ firstName.? ~ lastName.? ~ authMethod ~ hasher.? ~ password.? ~ salt.? ~ currentClubId.? ~ avatarUrl.? <>(User.apply _, User.unapply _)

  def autoInc = * returning pid


}
