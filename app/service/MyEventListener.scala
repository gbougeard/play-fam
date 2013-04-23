package service

import securesocial.core._
import play.api.mvc.{Session, RequestHeader}
import play.api.{Application, Logger}

import models.User
import models.Users._

import play.api.libs.json._

/**
 * A sample event listener
 */
class MyEventListener(app: Application) extends EventListener {
  override def id: String = "my_event_listener"

  def onEvent(event: Event, request: RequestHeader, session: Session): Option[Session] = {

    val eventName = event match {
      case e: LoginEvent => {
        val user = models.Users.findByOauth(event.user.id.providerId, event.user.id.id)
        ("login ", user)
      }
      case e: LogoutEvent => "logout"
      case e: SignUpEvent => "signup "
      case e: PasswordResetEvent => "password reset"
      case e: PasswordChangeEvent => "password change"
    }

    eventName match {
      case e: (String, User) => {
        Logger.info("login - traced %s event for user %s %s".format(e._1, event.user.fullName, e))
        Logger.info("login - put in session %s".format(Json.toJson(e._2).toString()))
        Some(session + ("user" -> Json.toJson(e._2).toString()))
      }
      case e: String => Logger.info("Traced %s event for user %s".format(e, event.user.fullName))
      None
    }

    // Not changing the session so just return None
    // if you wanted to change the session then you'd do something like
    // Some(session + ("your_key" -> "your_value"))
    //Some(session + ("user" -> Json.toJson(user).toString()))
    //    None
  }
}
