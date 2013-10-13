package service

import securesocial.core._
import play.api.mvc.{Session, RequestHeader}
import play.api.{Application, Logger}
import play.api.cache.Cache
import play.api.Play.current

import models.FamUser

import play.api.libs.json._

/**
 * A sample event listener
 */
class MyEventListener(app: Application) extends EventListener {
  override def id: String = "my_event_listener"

  def onEvent(event: Event, request: RequestHeader, session: Session): Option[Session] = {

    val eventName = event match {
      case e: LoginEvent => "login"
      case e: LogoutEvent => "logout"
      case e: SignUpEvent => "signup "
      case e: PasswordResetEvent => "password reset"
      case e: PasswordChangeEvent => "password change"
    }

    Logger.info("Traced %s event for user %s".format(event, event.user.fullName))

    // Not changing the session so just return None
    // if you wanted to change the session then you'd do something like
    // Some(session + ("your_key" -> "your_value"))
    event match {
      case e: LoginEvent => login(session, event.user)
      case e: LogoutEvent => None
      case e: SignUpEvent => None
      case e: PasswordResetEvent => None
      case e: PasswordChangeEvent => None
    }

    //    None
  }

  def login(session: Session, user: Identity) = {
    user match {
      case u: FamUser => {
        getRoles(u.pid)
        Some(session + ("email" -> u.email.getOrElse("")) + ("userId" -> u.pid.get.toString))
      }

      case u: Identity => Some(session + ("email" -> u.email.getOrElse("")))
    }
  }

  def getRoles(id: Option[Long]) = {
    id.map {
      u => {
        val roles = Json.toJson(models.Role.findByUserId(u))
        play.Logger.debug(s"getRoles for userId $u : $roles")
        Cache.set(s"roles.$u", roles)
        play.Logger.debug(s"Roles setted in cache : roles.$u => $roles -- ${Cache.get(s"roles.$u")}")
      }
    }
  }

}
