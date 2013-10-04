package controllers

import _root_.models.FamUser
import _root_.securesocial.core.Authorization
import _root_.securesocial.core.Identity

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.Play.current
import play.api.cache.Cache

import views._
import models.Role
import service.{Administrator, Permission, RequestUtil}
import play.api.libs.json.Json


object Application extends Controller with securesocial.core.SecureSocial {

  def me = SecuredAction {
    implicit request =>
//      play.Logger.debug(s"test index ${Cache.get("roles.1258")}")
      val famUser:FamUser = RequestUtil.getFamUser(request.user)
      val currentClub = famUser.currentClubId match {
          case Some(id) =>  models.Clubs.findById(id)
          case None => None
      }

    val player = models.Players.findByUserId(famUser.pid.get)
      Ok(views.html.me(request.user, currentClub, player))
  }

  def myPlayer(filter:String) = SecuredAction {
    implicit request =>
      val famUser:FamUser= RequestUtil.getFamUser(request.user)

      val player = models.Players.findByUserId(famUser.pid.get)
      val players = filter match {
        case "" => List()
        case _ => models.Players.find(filter)
      }
    play.Logger.debug(s"filter : $filter , $players")
      Ok(views.html.myPlayer(player, players, filter))
  }

  def createMyPlayer = SecuredAction {
    implicit request =>
      val famUser:FamUser= RequestUtil.getFamUser(request.user)

      val player = models.Players.findByUserId(famUser.pid.get)
      Ok(views.html.myPlayer(player, List(), ""))
  }


  def index = Action {
    implicit request =>
      Ok(views.html.index("Football Amateur Manager"))
  }

  def logout = Action {
    implicit request =>
      play.Logger.info("logout!")
      Ok("Bye").withNewSession
  }

  def deleteCacheById(id :Long ) = Action {
    implicit request =>
      play.Logger.info(s"delete cache for user $id")
      Cache.remove(s"roles.$id")
      Redirect(routes.Application.index())
  }

  def deleteCacheCurrentUser = SecuredAction {
    implicit request =>
      val id = request.user.identityId.userId
      play.Logger.info(s"delete cache for user $id")
      Cache.remove(s"roles.$id")
      Redirect(routes.Application.index())
  }

  // a sample action using the new authorization hook
  def onlyTwitter = SecuredAction(WithProvider("twitter")) {
    implicit request =>
    //
    //    Note: If you had a User class and returned an instance of it from UserService, this
    //          is how you would convert Identity to your own class:
    //
    //    request.user match {
    //      case user: User => // do whatever you need with your user class
    //      case _ => // did not get a User instance, should not happen,log error/thow exception
    //    }
      Ok("You can see this because you logged in using Twitter")
  }

  // a sample action using the new authorization hook
  def onlyGithub = SecuredAction(WithProvider("github")) {
    implicit request =>
    //
    //    Note: If you had a User class and returned an instance of it from UserService, this
    //          is how you would convert Identity to your own class:
    //
    //    request.user match {
    //      case user: User => // do whatever you need with your user class
    //      case _ => // did not get a User instance, should not happen,log error/thow exception
    //    }
      Ok("You can see this because you logged in using Github")
  }

  // -- Javascript routing
  def javascriptRoutes = Action {
    implicit request =>
      import routes.javascript._
      Ok(
        Routes.javascriptRouter("jsRoutes")(
          Places.gmapData,
          Places.jsonLikeZipcode,
          Places.jsonLikeCity,
          Places.gmapData,
          Places.jsonById,
          Places.update,
          Places.save,
          Events.view,
          Answers.jsonByEvent,
          Events.agenda,
          Events.save,
          Events.saveTeams,
          Events.update,
          Matchs.jsonById,
          Cards.jsonByMatchAndTeam,
          Goals.jsonByMatchAndTeam,
          Substitutions.jsonByMatchAndTeam,
          MatchTeams.jsonByMatchAndHome,
          MatchTeams.jsonByMatchAndAway,
          MatchTeams.jsonByMatchAndTeam,
          MatchPlayers.jsonByMatchAndTeam,
          Formations.saveItems
        )
      ).as(JAVASCRIPT)
  }

}

// An Authorization implementation that only authorizes uses that logged in using twitter
case class WithProvider(provider: String) extends Authorization {
  def isAuthorized(user: Identity) = {
    user.identityId.providerId == provider
  }
}

case class WithRightClub(id: Long) extends Authorization {
  def isAuthorized(user: Identity) = {
    val res = user match {
      case u: models.FamUser =>
       val rightClub = u.currentClubId.map {
          clubId => clubId == id
        } getOrElse false
        val admin =   u.pid.map {
          userId => {
            models.Roles.isUserInRole(userId, Set(Administrator))
          }
        } getOrElse false
        admin || rightClub
      case _ => false
    }
    res
  }
}

case class WithRoles(permissions : Set[Permission]) extends Authorization {
  def isAuthorized(user: Identity) = {
    play.Logger.debug(s"isAuthorized $user - $permissions")
    val res = user match {
      case u: models.FamUser =>
        u.pid.map {
          userId => {
           models.Roles.isUserInRole(userId, permissions)
          }
        } getOrElse false
      case _ => false
    }
    res
  }
}