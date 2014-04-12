package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.Logger

case class Substitution(id: Option[Long],
                        matchId: Long,
                        teamId: Long,
                        playerInId: Long,
                        playerOutId: Long,
                        time: Option[Long])

object Substitutions extends DAO{
  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Seq[(Substitution, Match, Team, Player, Player)] =  DB.withSession {
    implicit session =>
      val query = for {mp <- substitutions
                       if mp.matchId === idMatch
                       if mp.teamId === idTeam
                       m <- mp.matche
                       t <- mp.team
                       in <- mp.playerIn
                       out <- mp.playerOut

      } yield (mp, m, t, in, out)
      query.list.sortBy(_._1.time)
  }

  def insert(substitution: Substitution): Long =  DB.withSession {
    implicit session =>
      substitutions.insert(substitution)
  }

  def update(id: Long, substitution: Substitution) =  DB.withSession {
    implicit session =>
      val substitution2update = substitution.copy(Some(id))
      Logger.info("playe2update " + substitution2update)
      substitutions.where(_.id === id).update(substitution2update)
  }

  def delete(id: Long) =  DB.withSession {
    implicit session =>
      substitutions.where(_.id === id).delete
  }


}