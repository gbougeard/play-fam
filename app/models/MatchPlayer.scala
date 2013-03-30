package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger

case class MatchPlayer(matchId: Option[Long],
                       playerId: Option[Long],
                       teamId: Option[Long],
                       num: Option[Long],
                       captain: Boolean,
                       note: Option[Double],
                       timePlayed: Option[Long],
                       comments: Option[String])

// define tables
object MatchPlayers extends Table[MatchPlayer]("fam_match_player") {

  def matchId = column[Long]("id_match")

  def playerId = column[Long]("id_player")

  def teamId = column[Long]("id_team")

  def num = column[Long]("num")

  def captain = column[Boolean]("captain")

  def note = column[Double]("note")

  def timePlayed = column[Long]("time_played")

  def comments = column[String]("comments")

  def * = matchId.? ~ playerId.? ~ teamId.? ~ num.? ~ captain ~ note.? ~ timePlayed.? ~ comments.? <>(MatchPlayer, MatchPlayer.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, Matchs)(_.id)

  def player = foreignKey("PLAYER_FK", playerId, Players)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Seq[(MatchPlayer, Match, Player, Team)] = DB.withSession {
    implicit session => {
      val query = (
        for {mp <- MatchPlayers
             if mp.matchId === idMatch
             if mp.teamId === idTeam
             m <- mp.matche
             p <- mp.player
             t <- mp.team

        } yield (mp, m, p, t))
      query.list
    }
  }

  def insert(matchPlayer: MatchPlayer): Long = DB.withSession {
    implicit session => {
      MatchPlayers.insert((matchPlayer))
    }
  }

  def update(idMatch: Long, idPlayer:Long, matchPlayer: MatchPlayer) = DB.withSession {
    implicit session => {
//      val matchPlayer2update = matchPlayer.copy(Some(id))
//      Logger.info("playe2update " + matchPlayer2update)
//      MatchPlayers.where(_.id === id).update(matchPlayer2update)
    }
  }

  def delete(idMatch: Long, idPlayer:Long) = DB.withSession {
    implicit session => {
//      MatchPlayers.where(_.id === matchPlayerId).delete
    }
  }

}