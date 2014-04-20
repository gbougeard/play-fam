package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.Logger

case class MatchPlayer(matchId: Option[Long],
                       playerId: Option[Long],
                       teamId: Option[Long],
                       num: Option[Long],
                       captain: Boolean,
                       note: Option[Double],
                       timePlayed: Option[Long],
                       comments: Option[String])

object MatchPlayerJson {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val mpJsonFormat = Json.format[MatchPlayer]
  import models.PlayerJson._
  import models.TeamJson._
  import json.ImplicitGlobals._

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

object MatchPlayers extends DAO{

  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Seq[(MatchPlayer, Match, Player, Team)] =  DB.withSession {
    implicit session =>
      Logger.debug("slick matchPlayers "+idMatch +" "+idTeam)
      val query = for {mp <- matchPlayers
                       if mp.matchId === idMatch
                       if mp.teamId === idTeam
                       m <- mp.matche
                       p <- mp.player
                       t <- mp.team

      } yield (mp, m, p, t)
      query.list
  }

  def insert(matchPlayer: MatchPlayer): Long =  DB.withSession {
    implicit session =>
      matchPlayers.insert(matchPlayer)
  }

  def update(idMatch: Long, idPlayer:Long, matchPlayer: MatchPlayer) =  DB.withSession {
    implicit session =>
//      val matchPlayer2update = matchPlayer.copy(Some(id))
//      Logger.info("playe2update " + matchPlayer2update)
//      matchPlayers.where(_.id === id).update(matchPlayer2update)
  }

  def delete(idMatch: Long, idPlayer:Long) =  DB.withSession {
    implicit session =>
//      matchPlayers.where(_.id === matchPlayerId).delete
  }

}