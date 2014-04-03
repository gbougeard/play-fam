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

object MatchTeam{
  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Option[(MatchTeam, Team)] =  {
    implicit session:Session => {
      val query = for {mt <- MatchTeams
                       if mt.matchId === idMatch
                       if mt.teamId === idTeam
                       m <- mt.matche
                       t <- mt.team

      } yield (mt, t)
      query.firstOption
    }
  }

  def findByMatchAndHome(idMatch: Long): Option[(MatchTeam, Team)] =  {
    implicit session:Session => {
      val query = for {mt <- MatchTeams
                       if mt.matchId === idMatch
                       if mt.home === true
                       t <- mt.team
      } yield (mt, t)
      query.firstOption
    }
  }

  def findByMatchAndAway(idMatch: Long): Option[(MatchTeam, Team)] =  {
    implicit session:Session => {
      val query = for {mt <- MatchTeams
                       if mt.matchId === idMatch
                       if mt.home === false
                       t <- mt.team
      } yield (mt, t)
      query.firstOption
    }
  }

  def findByMatch(idMatch: Long): Seq[(MatchTeam, Match, Team)] =  {
    implicit session:Session => {
      val query = for {mt <- MatchTeams
                       if mt.matchId === idMatch
                       m <- mt.matche
                       t <- mt.team

      } yield (mt, m, t)
      query.list
    }
  }

  def findByTeam(idTeam: Long): Seq[(MatchTeam, Match, Team)] =  {
    implicit session:Session => {
      val query = for {mt <- MatchTeams
                       if mt.teamId === idTeam
                       m <- mt.matche
                       t <- mt.team

      } yield (mt, m, t)
      query.list
    }
  }

  def insert(mt: MatchTeam): Long =  {
    implicit session:Session => {
      MatchTeams.insert(mt)
    }
  }

  def update(id: Long, m: MatchTeam) =  {
    implicit session:Session => {
      val matchTeam2update = m.copy(Some(id))
      //      MatchTeams.where(_.id === id).update(matchTeam2update)
    }
  }

  def delete(matchTeamId: Long) =  {
    implicit session:Session => {
      //      MatchTeams.where(_.id === matchTeamId).delete
    }
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