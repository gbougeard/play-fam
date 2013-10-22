package controllers

import _root_.securesocial.core.Authorization
import _root_.securesocial.core.Identity

import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import models._
import play.api.libs.json._

import play.api.Logger
import service.{Coach,Administrator}

object Matchs extends Controller with securesocial.core.SecureSocial {


  /**
   * Describe the match form (used in both edit and create screens).
   */
  val matchForm = Form(
    mapping(
      "id" -> optional(longNumber),
      "fixtureId" -> optional(longNumber),
      "competitionId" -> longNumber,
      "eventId" -> optional(longNumber)
    )
      (Match.apply)(Match.unapply)
  )

  // -- Actions

  def list(page: Int, orderBy: Int) = Action {
    implicit request =>
      val matchs = Match.findPage(page, orderBy)
      val html = views.html.matchs.list("Liste des matchs", matchs, orderBy)
      Ok(html)
  }

  def byEventId(id: Long) = Action {
    implicit request =>
      Match.findByEventId(id).map{
        m => Redirect(routes.Matchs.view(m.id.getOrElse(0)))
      } getOrElse (NotFound)
  }

  def view(id: Long) = Action {
    implicit request =>
      Match.findById(id).map {
        m => {
          Event.findById(m.eventId.getOrElse(0)).map {
            case (event, typEvent, eventStatus) => {
              MatchTeam.findByMatchAndHome(id).map {
                case (home, homeTeam) => {
                  val homeGoals = Goal.findByMatchAndTeam(id,  homeTeam.id.getOrElse(0))
                  val homePlayers = MatchPlayer.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
                  val homeSubs = Substitution.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
                  val homeCards = Card.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))

                  MatchTeam.findByMatchAndAway(id).map {
                  case  (away, awayTeam ) => {
                    val awayGoals = Goal.findByMatchAndTeam(id,  awayTeam.id.getOrElse(0))
                    val awayPlayers = MatchPlayer.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
                    val awaySubs = Substitution.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
                    val awayCards = Card.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
                      Ok(views.html.matchs.view("View Match", m, event, (home,homeTeam), (away, awayTeam), homeGoals, awayGoals,  homePlayers, awayPlayers, homeSubs, awaySubs, homeCards, awayCards))
                    }
                  } getOrElse (NotFound("Away team not found"))
                }
              } getOrElse (NotFound("Home team not found"))
            }
          } getOrElse (NotFound("Event not found"))
        }
      } getOrElse (NotFound("Match not Found"))
  }

  def debrief(idMatch:Long, idTeam: Long) =  Action{ //SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      Match.findById(idMatch).map {
        m => {
          Event.findById(m.eventId.getOrElse(0)).map {
            case (event, typEvent, eventStatus) => {
              Ok(views.html.matchs.debrief("Debrief Match", m, event, idMatch, idTeam))
//              MatchTeam.findByMatchAndHome(id).map {
//                case (home, homeTeam) => {
//                  val homeGoals = Goal.findByMatchAndTeam(id,  homeTeam.id.getOrElse(0))
//                  val homePlayers = MatchPlayer.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
//                  val homeSubs = Substitution.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
//                  val homeCards = Card.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
//
//                  MatchTeam.findByMatchAndAway(id).map {
//                  case  (away, awayTeam ) => {
//                    val awayGoals = Goal.findByMatchAndTeam(id,  awayTeam.id.getOrElse(0))
//                    val awayPlayers = MatchPlayer.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
//                    val awaySubs = Substitution.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
//                    val awayCards = Card.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
//                      Ok(views.html.matchs.edit("Debrief Match", m, event, (home,homeTeam), (away, awayTeam), homeGoals, awayGoals,  homePlayers, awayPlayers, homeSubs, awaySubs, homeCards, awayCards))
//                    }
//                  } getOrElse (NotFound)
//                }
//              } getOrElse (NotFound)
            }
          } getOrElse (NotFound)
        }
      } getOrElse (NotFound)
  }

  def prepare(idMatch:Long, idTeam: Long) =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      Match.findById(idMatch).map {
        m => {
          Event.findById(m.eventId.getOrElse(0)).map {
            case (event, typEvent, eventStatus) => {
              Ok(views.html.matchs.prepare("Prepare Match", m, event, idMatch, idTeam))
//              MatchTeam.findByMatchAndHome(id).map {
//                case (home, homeTeam) => {
//                  val homeGoals = Goal.findByMatchAndTeam(id,  homeTeam.id.getOrElse(0))
//                  val homePlayers = MatchPlayer.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
//                  val homeSubs = Substitution.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
//                  val homeCards = Card.findByMatchAndTeam(id, homeTeam.id.getOrElse(0))
//
//                  MatchTeam.findByMatchAndAway(id).map {
//                  case  (away, awayTeam ) => {
//                    val awayGoals = Goal.findByMatchAndTeam(id,  awayTeam.id.getOrElse(0))
//                    val awayPlayers = MatchPlayer.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
//                    val awaySubs = Substitution.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
//                    val awayCards = Card.findByMatchAndTeam(id, awayTeam.id.getOrElse(0))
//                      Ok(views.html.matchs.edit("Debrief Match", m, event, (home,homeTeam), (away, awayTeam), homeGoals, awayGoals,  homePlayers, awayPlayers, homeSubs, awaySubs, homeCards, awayCards))
//                    }
//                  } getOrElse (NotFound)
//                }
//              } getOrElse (NotFound)
            }
          } getOrElse (NotFound)
        }
      } getOrElse (NotFound)
  }

//  def edit(id: Long) = SecuredAction {
//    implicit request =>
//      Match.findById(id).map {
//        m => Ok(views.html.matchs.edit("Edit Match", id, matchForm.fill(m)))
//      } getOrElse (NotFound)
//  }

  /**
   * Handle the 'edit form' submission
   *
   * @param id Id of the computer to edit
   */
//  def update(id: Long) = SecuredAction {
//    implicit request =>
//      Logger.info("update match " + id)
//      matchForm.bindFromRequest.fold(
//        formWithErrors => BadRequest(views.html.matchs.edit("Edit Match - errors", id, formWithErrors)),
//        m => {
//          Match.update(id, m)
//          Redirect(routes.Matchs.debrief(id)).flashing("success" -> "Match %s has been updated".format(m.id))
//          //          Redirect(routes.Matchs.list(0, 2))
//        }
//      )
//  }

  /**
   * Display the 'new computer form'.
   */
//  def create = SecuredAction {
//    implicit request =>
//      Ok(views.html.matchs.create("New Match", matchForm))
//  }

  /**
   * Handle the 'new computer form' submission.
   */
  def save =  SecuredAction(WithRoles(Set(Coach)))  {
    implicit request =>
      matchForm.bindFromRequest.fold(
        formWithErrors => BadRequest(views.html.matchs.create("New Match - errors", formWithErrors)),
        m => {
          Match.insert(m)
          //        Home.flashing("success" -> "Match %s has been created".format(match.name))
          Redirect(routes.Matchs.list(0, 2))
        }
      )
  }

  /**
   * Handle computer deletion.
   */
  def delete(id: Long) =  SecuredAction(WithRoles(Set(Administrator)))  {
    implicit request =>
      Match.delete(id)
      Redirect(routes.Matchs.list(0, 0)).flashing("success" -> "Match has been deleted")
  }

  def jsonById(id:Long) = Action {
    implicit request =>
      Match.findById(id).map {
       m => Ok(Json.toJson(m))
      } getOrElse(NotFound)
  }

}