package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Match._
import models.Player._
import models.Team._

import play.api.Logger
import models.database.MatchPlayers

case class MatchPlayer(matchId: Option[Long],
                       playerId: Option[Long],
                       teamId: Option[Long],
                       num: Option[Long],
                       captain: Boolean,
                       note: Option[Double],
                       timePlayed: Option[Long],
                       comments: Option[String])

object MatchPlayer{

  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Seq[(MatchPlayer, Match, Player, Team)] = DB.withSession {
    implicit session:Session => {
      Logger.debug("slick MatchPlayers "+idMatch +" "+idTeam)
      val query = for {mp <- MatchPlayers
                       if mp.matchId === idMatch
                       if mp.teamId === idTeam
                       m <- mp.matche
                       p <- mp.player
                       t <- mp.team

      } yield (mp, m, p, t)
      query.list
    }
  }

  def insert(matchPlayer: MatchPlayer): Long = DB.withSession {
    implicit session:Session => {
      MatchPlayers.insert(matchPlayer)
    }
  }

  def update(idMatch: Long, idPlayer:Long, matchPlayer: MatchPlayer) = DB.withSession {
    implicit session:Session => {
//      val matchPlayer2update = matchPlayer.copy(Some(id))
//      Logger.info("playe2update " + matchPlayer2update)
//      MatchPlayers.where(_.id === id).update(matchPlayer2update)
    }
  }

  def delete(idMatch: Long, idPlayer:Long) = DB.withSession {
    implicit session:Session => {
//      MatchPlayers.where(_.id === matchPlayerId).delete
    }
  }

  implicit val mpFormat = Json.format[MatchPlayer]

  implicit val mpCompleteReads: Reads[(MatchPlayer, Match, Player, Team)] = (
    (__ \ 'matchplayer).read[MatchPlayer] ~
      (__ \ 'match).read[Match] ~
      (__ \ 'player).read[Player] ~
      (__ \ 'team).read[Team]
    ) tupled

  implicit val mpCompleteWrites: Writes[(MatchPlayer, Match, Player, Team)] = (
    (__ \ 'matchplayer).write[MatchPlayer] ~
      (__ \ 'match).write[Match] ~
      (__ \ 'player).write[Player] ~
      (__ \ 'team).write[Team]
    ) tupled

}