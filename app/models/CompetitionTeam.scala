package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger

case class CompetitionTeam(id: Option[Long],
                           competitionId: Long,
                           teamId: Long)

// define tables
object CompetitionTeams extends Table[CompetitionTeam]("fam_competition_team") {

  def id = column[Long]("id_competition_team", O.PrimaryKey, O.AutoInc)

  def competitionId = column[Long]("id_season_competition")

  def teamId = column[Long]("id_team")

  def * = id.? ~ competitionId ~ teamId <>(CompetitionTeam, CompetitionTeam.unapply _)

  def autoInc = id.? ~ competitionId ~ teamId <>(CompetitionTeam, CompetitionTeam.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def competition = foreignKey("COMPETITION_FK", competitionId, SeasonCompetitions)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

  lazy val pageSize = 10

  //  def findPage(page: Int = 0, orderField: Int): Page[(Competition)] = {
  //
  //    val offset = pageSize * page
  //
  //    DB.withSession {
  //      implicit session => {
  //        val competitions = (
  //          for {t <- Competitions
  //            .sortBy(competition => orderField match {
  //            case 1 => competition.firstName.asc
  //            case -1 => competition.firstName.desc
  //            case 2 => competition.lastName.asc
  //            case -2 => competition.lastName.desc
  //            case 3 => competition.email.asc
  //            case -3 => competition.email.desc
  //          })
  //            .drop(offset)
  //            .take(pageSize)
  //          } yield (t)).list
  //
  //        val totalRows = (for {t <- Competitions} yield t.id).list.size
  //        Page(competitions, page, offset, totalRows)
  //      }
  //    }
  //  }

  def findByTeam(id: Long): Seq[(CompetitionTeam, SeasonCompetition, Team)] = DB.withSession {
    implicit session => {
      val query = (
        for {ps <- CompetitionTeams
             if ps.teamId === id
             p <- ps.competition
             s <- ps.team

        } yield (ps, p, s))
      query.list
    }
  }

  def findByCompetition(id: Long): Seq[(CompetitionTeam, SeasonCompetition, Team)] = DB.withSession {
    implicit session => {
      val query = (
        for {ps <- CompetitionTeams
             if ps.competitionId === id
             p <- ps.competition
             s <- ps.team

        } yield (ps, p, s))
      query.list
    }
  }

  def insert(competitionTeam: CompetitionTeam): Long = DB.withSession {
    implicit session => {
      CompetitionTeams.autoInc.insert((competitionTeam))
    }
  }

  def update(id: Long, competition: CompetitionTeam) = DB.withSession {
    implicit session => {
      val competition2update = competition.copy(Some(id))
      //        Logger.info("playe2update " + competition2update)
      CompetitionTeams.where(_.id === id).update(competition2update)
    }
  }

  def delete(competitionId: Long) = DB.withSession {
    implicit session => {
      CompetitionTeams.where(_.id === competitionId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)

  //  def options: Seq[(String, String)] = DB.withSession {
  //    implicit session =>
  //      val query = (for {
  //        item <- Competitions
  //      } yield (item.id, item.firstName + " " + item.lastName)
  //        ).sortBy(_._2)
  //      query.list.map(row => (row._1.toString, row._2))
  //  }

  implicit val competitionTeamFormat = Json.format[CompetitionTeam]
}