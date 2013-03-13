package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._


case class Ranking(competitionId: Long,
                   clubId: Long,
                   teamId: Long,
                   team: String,
                   points: Int,
                   victory: Int,
                   defeat: Int,
                   draw: Int,
                   played: Int,
                   goalScored: Int,
                   goalShipped: Int)


// define tables
object Rankings extends Table[Ranking]("v_rankings") {

  def competitionId = column[Long]("id_season_competition")

  def clubId = column[Long]("id_club")

  def teamId = column[Long]("id_team")

  def team = column[String]("lib_team")

  def points = column[Int]("points")

  def victory = column[Int]("victory")

  def defeat = column[Int]("defeat")

  def draw = column[Int]("draw")

  def played = column[Int]("played")

  def goalScored = column[Int]("goal_scored")

  def goalShipped = column[Int]("goal_shipped")

  def * = competitionId ~ clubId ~ teamId ~ team ~ points ~ victory ~ defeat ~ draw ~ played ~ goalScored ~ goalShipped <>(Ranking, Ranking.unapply _)


  val byCompetition = createFinderBy(_.competitionId)
  val byClub = createFinderBy(_.clubId)
  val byTeam = createFinderBy(_.teamId)

  lazy val pageSize = 10

  def findAll: Seq[Ranking] = DB.withSession {
    implicit session => {
      (for (c <- Rankings.sortBy(_.points.desc)) yield c).list
    }
  }

  //  def findPage(page: Int = 0, orderField: Int): Page[Ranking] = {
  //
  //    val offset = pageSize * page
  //
  //    DB.withSession {
  //      implicit session =>
  //        val clubs = (
  //          for {c <- Rankings
  //            .sortBy(club => orderField match {
  //            case 1 => club.code.asc
  //            case -1 => club.code.desc
  //            case 2 => club.name.asc
  //            case -2 => club.name.desc
  //          })
  //            .drop(offset)
  //            .take(pageSize)
  //          } yield c).list
  //
  //        val totalRows = (for (c <- Rankings) yield c.id).list.size
  //        Page(clubs, page, offset, totalRows)
  //    }
  //  }

  def findByCompetition(id: Long): Seq[Ranking] = DB.withSession {
    implicit session => {
      (for {
        c <- Rankings.sortBy(_.points.desc)
        if c.competitionId === id
      } yield c).list
    }
  }

  def findByClub(id: Long): Seq[Ranking] = DB.withSession {
    implicit session => {
      (for {
        c <- Rankings.sortBy(_.points.desc)
        if c.clubId === id
      } yield c).list
    }
  }

  def findByTeam(id: Long): Seq[Ranking] = DB.withSession {
    implicit session => {
      (for {
        c <- Rankings.sortBy(_.points.desc)
        if c.teamId === id
      } yield c).list
    }
  }

  implicit val rankingFormat = Json.format[Ranking]

}

