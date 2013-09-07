package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Teams._

import play.api.Logger

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

// define tables
object MatchTeams extends Table[MatchTeam]("fam_match_team") {

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def formationId = column[Long]("id_formation")

  def home = column[Boolean]("home")

  def defeat = column[Boolean]("defeat")

  def draw = column[Boolean]("draw")

  def victory = column[Boolean]("victory")

  def goalScored = column[Long]("goal_scored")

  def goalShipped = column[Long]("goal_shipped")

  def points = column[Long]("points")

  def resume = column[String]("resume")

  def draft = column[Boolean]("draft")

  def * = matchId.? ~
    teamId.? ~
    formationId.? ~
    home ~
    defeat.? ~
    draw.? ~
    victory.? ~
    goalScored.? ~
    goalShipped.? ~
    points.? ~
    resume.? ~
    draft.? <>(MatchTeam, MatchTeam.unapply _)


  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, Matchs)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

  //  def formation = foreignKey("FORMATION_FK", formationId, Events)(_.id)

  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Option[(MatchTeam, Team)] = DB.withSession {
    implicit session:Session => {
      val query = (
        for {mt <- MatchTeams
             if mt.matchId === idMatch
             if mt.teamId === idTeam
             m <- mt.matche
             t <- mt.team

        } yield (mt, t))
      query.firstOption
    }
  }

  def findByMatchAndHome(idMatch: Long): Option[(MatchTeam, Team)] = DB.withSession {
    implicit session:Session => {
      val query = (
        for {mt <- MatchTeams
             if mt.matchId === idMatch
             if mt.home === true
             t <- mt.team
        } yield (mt, t))
      query.firstOption
    }
  }

  def findByMatchAndAway(idMatch: Long): Option[(MatchTeam, Team)] = DB.withSession {
    implicit session:Session => {
      val query = (
        for {mt <- MatchTeams
             if mt.matchId === idMatch
             if mt.home === false
             t <- mt.team
        } yield (mt, t))
      query.firstOption
    }
  }

  def findByMatch(idMatch: Long): Seq[(MatchTeam, Match, Team)] = DB.withSession {
    implicit session:Session => {
      val query = (
        for {mt <- MatchTeams
             if mt.matchId === idMatch
             m <- mt.matche
             t <- mt.team

        } yield (mt, m, t))
      query.list
    }
  }

  def findByTeam(idTeam: Long): Seq[(MatchTeam, Match, Team)] = DB.withSession {
    implicit session:Session => {
      val query = (
        for {mt <- MatchTeams
             if mt.teamId === idTeam
             m <- mt.matche
             t <- mt.team

        } yield (mt, m, t))
      query.list
    }
  }

  def insert(m: MatchTeam): Long = DB.withSession {
    implicit session:Session => {
      MatchTeams.insert((m))
    }
  }

  def update(id: Long, m: MatchTeam) = DB.withSession {
    implicit session:Session => {
      val matchTeam2update = m.copy(Some(id))
      Logger.info("playe2update " + matchTeam2update)
      //      MatchTeams.where(_.id === id).update(matchTeam2update)
    }
  }

  def delete(matchTeamId: Long) = DB.withSession {
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