package models

import securesocial.core._
import securesocial.core.UserId
import securesocial.core.OAuth2Info
import securesocial.core.OAuth1Info
import securesocial.core.PasswordInfo
import scala.Some
import play.api.libs.Codecs

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 27/04/13
 * Time: 15:31
 * To change this template use File | Settings | File Templates.
 */

case class FamUser(
                    pid: Option[Long] = None,
                    userId: String,
                    providerId: String,
                    email: Option[String],
                    firstName: String,
                    lastName: String,
                    fullName: String,
                    avatarUrl: Option[String],
                    authMethod: AuthenticationMethod,
                    oAuth1Info: Option[OAuth1Info] = None,
                    oAuth2Info: Option[OAuth2Info] = None,
                    passwordInfo: Option[PasswordInfo] = None,
                    currentClubId: Option[Long] = None
                    ) extends Identity {
  def id: UserId = UserId(userId, providerId)

  //  def fullName: String = s"$firstName $lastName"

  def gravatar: Option[String] = email.map {
    e => s"http://www.gravatar.com/avatar/${Codecs.md5(e.getBytes)}.png"
  }
}

object FamUser {
  def fromIdentity(user: Identity) = {
    FamUser(
      pid = None,
      userId = user.id.id,
      providerId = user.id.providerId,
      email = user.email,
      firstName = user.firstName,
      lastName = user.lastName,
      fullName = user.fullName,
      avatarUrl = user.avatarUrl,
      authMethod = user.authMethod,
      oAuth1Info = user.oAuth1Info,
      oAuth2Info = user.oAuth2Info,
      passwordInfo = user.passwordInfo
    )
  }

  def fromUser(user: User) = {
    FamUser(
      pid = None,
      userId = user.userId,
      providerId = user.providerId,
      email = user.email,
      firstName = user.firstName,
      lastName = user.lastName,
      fullName = user.firstName + " " + user.lastName,
      avatarUrl = user.avatarUrl,
      authMethod = user.authMethod,
      oAuth1Info = None, //Some(OAuth1Info(user.token, user.secret)),
      oAuth2Info = None, //user.oAuth2Info,
      passwordInfo = user.password.map {
        p => Some(PasswordInfo(user.hasher.getOrElse(""), p, user.salt))
      } getOrElse (None),
      currentClubId = user.currentClubId
    )
  }
}
