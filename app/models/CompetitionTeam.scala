package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger
import database.CompetitionTeams

case class CompetitionTeam(id: Option[Long],
                           competitionId: Long,
                           teamId: Long)
object CompetitionTeams extends DAO{
  lazy val pageSize = 10

  //  def findPage(page: Int = 0, orderField: Int): Page[(Competition)] = {
  //
  //    val offset = pageSize * page
  //
  //     {
  //      implicit session:Session => {
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

  def findByTeam(id: Long)(implicit session: Session): Seq[(CompetitionTeam, SeasonCompetition, Team)] =  {
      val query = (
        for {ps <- competitionTeams
             if ps.teamId === id
             p <- ps.competition
             s <- ps.team

        } yield (ps, p, s))
  }

  def findByCompetition(id: Long)(implicit session: Session): Seq[(CompetitionTeam, SeasonCompetition, Team)] =  {
      val query = (
        for {ps <- competitionTeams
             if ps.competitionId === id
             p <- ps.competition
             s <- ps.team

        } yield (ps, p, s))
      query.list
  }

  def insert(competitionTeam: CompetitionTeam)(implicit session: Session): Long =  {
      competitionTeams.insert(competitionTeam)
  }

  def update(id: Long, competition: CompetitionTeam)(implicit session: Session) =  {
      val competition2update = competition.copy(Some(id))
      //        Logger.info("playe2update " + competition2update)
      competitionTeams.where(_.id === id).update(competition2update)
  }

  def delete(competitionId: Long)(implicit session: Session) =  {
      competitionTeams.where(_.id === competitionId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)

  //  def options: Seq[(String, String)] =  {
  //    implicit session:Session =>
  //      val query = (for {
  //        item <- Competitions
  //      } yield (item.id, item.firstName + " " + item.lastName)
  //        ).sortBy(_._2)
  //      query.list.map(row => (row._1.toString, row._2))
  //  }

  implicit val competitionTeamFormat = Json.format[CompetitionTeam]
}