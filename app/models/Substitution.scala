package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger

case class Substitution(id: Option[Long],
                        matchId: Long,
                        teamId: Long,
                        playerInId: Long,
                        playerOutId: Long,
                        time: Option[Long])

// define tables
object Substitutions extends Table[Substitution]("fam_substitution") {

  def id = column[Long]("id_substitution")

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def playerInId = column[Long]("id_player_in")

  def playerOutId = column[Long]("id_player_out")

  def time = column[Long]("substitution_time")

  def * = id.? ~ matchId ~ teamId ~ playerInId ~ playerOutId ~ time.? <>(Substitution, Substitution.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, Matchs)(_.id)

  def playerIn = foreignKey("PLAYER_IN_FK", playerInId, Players)(_.id)

  def playerOut = foreignKey("PLAYER_OUT_FK", playerOutId, Players)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Seq[(Substitution, Match, Team, Player, Player)] = DB.withSession {
    implicit session => {
      val query = (
        for {mp <- Substitutions
             if mp.matchId === idMatch
             if mp.teamId === idTeam
             m <- mp.matche
             t <- mp.team
             in <- mp.playerIn
             out <- mp.playerOut

        } yield (mp, m, t, in, out))
      query.list.sortBy(_._1.time)
    }
  }

  def insert(substitution: Substitution): Long = DB.withSession {
    implicit session => {
      Substitutions.insert((substitution))
    }
  }

  def update(id: Long, substitution: Substitution) = DB.withSession {
    implicit session => {
      val substitution2update = substitution.copy(Some(id))
      Logger.info("playe2update " + substitution2update)
      Substitutions.where(_.id === id).update(substitution2update)
    }
  }

  def delete(id: Long) = DB.withSession {
    implicit session => {
      Substitutions.where(_.id === id).delete
    }
  }

}