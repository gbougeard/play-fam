package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Team._

import play.api.Logger
import database.MatchTeams

case class MatchTeam(matchId: Option[Long],
                     teamId: Option[Long],
                     formationId: Option[Long],
                     home: Boolean,
                     defeat: Option[Boolean],
                     draw: Option[Boolean],
                     victory: Option[Boolean],
                     goalScored: Option[Long],
                     goalShipped: Option[Long],
                     points: Option[Long],
                     resume: Option[String],
                     draft: Option[Boolean]
                      )

object MatchTeams extends DAO{
  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long)(implicit session: Session): Option[(MatchTeam, Team)] =  {
      val query = for {mt <- matchTeams
                       if mt.matchId === idMatch
                       if mt.teamId === idTeam
                       m <- mt.matche
                       t <- mt.team

      } yield (mt, t)
      query.firstOption
  }

  def findByMatchAndHome(idMatch: Long)(implicit session: Session): Option[(MatchTeam, Team)] =  {
      val query = for {mt <- matchTeams
                       if mt.matchId === idMatch
                       if mt.home === true
                       t <- mt.team
      } yield (mt, t)
      query.firstOption
  }

  def findByMatchAndAway(idMatch: Long)(implicit session: Session): Option[(MatchTeam, Team)] =  {
      val query = for {mt <- matchTeams
                       if mt.matchId === idMatch
                       if mt.home === false
                       t <- mt.team
      } yield (mt, t)
      query.firstOption
  }

  def findByMatch(idMatch: Long)(implicit session: Session): Seq[(MatchTeam, Match, Team)] =  {
      val query = for {mt <- matchTeams
                       if mt.matchId === idMatch
                       m <- mt.matche
                       t <- mt.team

      } yield (mt, m, t)
      query.list
  }

  def findByTeam(idTeam: Long)(implicit session: Session): Seq[(MatchTeam, Match, Team)] =  {
      val query = for {mt <- matchTeams
                       if mt.teamId === idTeam
                       m <- mt.matche
                       t <- mt.team

      } yield (mt, m, t)
      query.list
  }

  def insert(mt: MatchTeam)(implicit session: Session): Long =  {
      matchTeams.insert(mt)
  }

  def update(id: Long, m: MatchTeam)(implicit session: Session) =  {
      val matchTeam2update = m.copy(Some(id))
      //      matchTeams.where(_.id === id).update(matchTeam2update)
  }

  def delete(matchTeamId: Long)(implicit session: Session) =  {
      //      matchTeams.where(_.id === matchTeamId).delete
  }

  implicit val mtFormat = Json.format[MatchTeam]

  implicit val mtCompleteReads: Reads[(MatchTeam, Team)] = (
    (__ \ 'matchteam).read[MatchTeam] ~
      (__ \ 'team).read[Team]
    ) tupled

  implicit val mtCompleteWrites: Writes[(MatchTeam, Team)] = (
    (__ \ 'matchteam).write[MatchTeam] ~
      (__ \ 'team).write[Team]
    ) tupled

}