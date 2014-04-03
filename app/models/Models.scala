package models

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.libs.json.Writes
import scala.slick.lifted.TableQuery
import models.database._

// Use the implicit threadLocalSession


/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

object Page {
  implicit def writes[A : Writes]: Writes[Page[A]] = (
    (__ \ 'items).write[Seq[A]] ~
      (__ \ 'page).write[Int] ~
      (__ \ 'offset).write[Long] ~
      (__ \ 'total).write[Long]
    ) (unlift(Page.unapply[A]))

}

private[models] trait DAO {
  val answers = TableQuery[Answers]
  val cards = TableQuery[Cards]
  val categories = TableQuery[Categories]
  val cities = TableQuery[Cities]
  val clubs = TableQuery[Clubs]
  val competitionTeams = TableQuery[CompetitionTeams]
  val countries = TableQuery[Countries]
  val events = TableQuery[Events]
  val eventCategories = TableQuery[EventCategories]
  val eventStatuses = TableQuery[EventStatuses]
  val eventTeams = TableQuery[EventTeams]
  val famUsers = TableQuery[FamUser]
  val fixtures = TableQuery[Fixtures]
  val formations = TableQuery[Formations]
  val formationItems = TableQuery[FormationItems]
  val goals = TableQuery[Goals]
  val groups = TableQuery[Groups]
  val matches = TableQuery[Matches]
  val matchPlayers = TableQuery[MatchPlayers]
  val matchTeams = TableQuery[MatchTeams]
  val places = TableQuery[Places]
  val placeClubs = TableQuery[PlaceClubs]
  val players = TableQuery[Players]
  val playerPositions = TableQuery[PlayerPositions]
  val playerSeasons = TableQuery[PlayerSeasons]
  val positions = TableQuery[Positions]
  val provinces = TableQuery[Provinces]
  val rankings = TableQuery[Rankings]
  val roles = TableQuery[Roles]
  val scales = TableQuery[Scales]
  val seasons = TableQuery[Seasons]
  val seasonCompetitions = TableQuery[SeasonCompetitions]
  val states = TableQuery[States]
  val substitutions = TableQuery[Substitutions]
  val teams = TableQuery[Teams]
  val typAnswers = TableQuery[TypAnswers]
  val typCards = TableQuery[TypCards]
  val typCompetitions = TableQuery[TypCompetitions]
  val typEvents = TableQuery[TypEvents]
  val typMatches = TableQuery[TypMatches]
  val users = TableQuery[Users]
}

import scala.slick.lifted.CanBeQueryCondition

// optionally filter on a column with a supplied predicate
case class MaybeFilter[X, Y](val query: scala.slick.lifted.Query[X, Y]) {
  def filter[T, R: CanBeQueryCondition](data: Option[T])(f: T => X => R) = {
    data.map(v => MaybeFilter(query.filter(f(v)))).getOrElse(this)
  }
}





