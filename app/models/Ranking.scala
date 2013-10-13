package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._
import database.Rankings

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


object Ranking{
  lazy val pageSize = 10

  def findAll: Seq[Ranking] = DB.withSession {
    implicit session:Session => {
      (for (c <- Rankings.sortBy(_.points.desc)) yield c).list
    }
  }

  //  def findPage(page: Int = 0, orderField: Int): Page[Ranking] = {
  //
  //    val offset = pageSize * page
  //
  //    DB.withSession {
  //      implicit session:Session =>
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
    implicit session:Session => {
      (for {
        c <- Rankings.sortBy(_.points.desc)
        if c.competitionId === id
      } yield c).list
    }
  }

  def findByClub(id: Long): Seq[Ranking] = DB.withSession {
    implicit session:Session => {
      (for {
        c <- Rankings.sortBy(_.points.desc)
        if c.clubId === id
      } yield c).list
    }
  }

  def findByTeam(id: Long): Seq[Ranking] = DB.withSession {
    implicit session:Session => {
      (for {
        c <- Rankings.sortBy(_.points.desc)
        if c.teamId === id
      } yield c).list
    }
  }

  implicit val rankingFormat = Json.format[Ranking]

}

