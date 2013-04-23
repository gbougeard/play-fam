package service

import models.User
import securesocial.core._
import play.api.libs.json.Json

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 23/04/13
 * Time: 21:35
 * To change this template use File | Settings | File Templates.
 */
case class FamUser(id: UserId,
                   firstName: String,
                   lastName: String,
                   fullName: String,
                   email: Option[String],
                   avatarUrl: Option[String],
                   authMethod: AuthenticationMethod,
                   oAuth1Info: Option[OAuth1Info] = None,
                   oAuth2Info: Option[OAuth2Info] = None,
                   passwordInfo: Option[PasswordInfo] = None,
                   user: Option[User] = None) extends Identity

object FamUser {
  def apply(i: Identity, user: User): FamUser = {
    FamUser(i.id,
      user.firstName,
      user.lastName,
      user.firstName + " " + user.lastName,
      Some(user.email),
      i.avatarUrl,
      i.authMethod,
      i.oAuth1Info,
      i.oAuth2Info,
      i.passwordInfo,
     Some(user)
    )
  }

}
