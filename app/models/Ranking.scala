package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


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


object Rankings extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[Ranking] =  DB.withSession {
    implicit session =>
      (for (c <- rankings.sortBy(_.points.desc)) yield c).list
  }

  //  def findPage(page: Int = 0, orderField: Int): Page[Ranking] = {
  //
  //    val offset = pageSize * page
  //
  //     {
  //      implicit session:Session =>
  //        val clubs = (
  //          for {c <- rankings
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
  //        val totalRows = (for (c <- rankings) yield c.id).list.size
  //        Page(clubs, page, offset, totalRows)
  //    }
  //  }

  def findByCompetition(id: Long): Seq[Ranking] =  DB.withSession {
    implicit session =>
      (for {
        c <- rankings.sortBy(_.points.desc)
        if c.competitionId === id
      } yield c).list
  }

  def findByClub(id: Long): Seq[Ranking] =  DB.withSession {
    implicit session =>
      (for {
        c <- rankings.sortBy(_.points.desc)
        if c.clubId === id
      } yield c).list
  }

  def findByTeam(id: Long): Seq[Ranking] =  DB.withSession {
    implicit session =>
      (for {
        c <- rankings.sortBy(_.points.desc)
        if c.teamId === id
      } yield c).list
  }


}

