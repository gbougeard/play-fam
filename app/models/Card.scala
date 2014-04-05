package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Match._
import models.Team._
import models.Player._
import models.TypCard._

import play.api.Logger

import database.Cards

case class Card(id: Option[Long],
                        matchId: Long,
                        teamId: Long,
                        playerId: Long,
                        typCardId: Long,
                        time: Option[Long])

object Cards extends DAO{

  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Seq[(Card, Match, Team, Player, TypCard)] = DB.withSession {
    implicit session =>
      val query = for {mp <- cards
                       if mp.matchId === idMatch
                       if mp.teamId === idTeam
                       m <- mp.matche
                       t <- mp.team
                       p <- mp.player
                       tc <- mp.typCard

      } yield (mp, m, t, p, tc)
      query.list.sortBy(_._1.time)
  }

  def insert(card: Card): Long = DB.withSession {
    implicit session =>
      cards.insert(card)
  }

  def update(id: Long, card: Card) = DB.withSession {
    implicit session =>
      val card2update = card.copy(Some(id))
//      Logger.info("playe2update " + card2update)
      cards.where(_.id === id).update(card2update)
  }

  def delete(id: Long) = DB.withSession {
    implicit session =>
      cards.where(_.id === id).delete
  }

  implicit val cardFormat = Json.format[Card]

  implicit val cardCompleteReads: Reads[(Card, Match, Team, Player, TypCard)] = (
    (__ \ 'card).read[Card] ~
      (__ \ 'match).read[Match] ~
      (__ \ 'team).read[Team] ~
      (__ \ 'player).read[Player] ~
      (__ \ 'typcard).read[TypCard]
    ) tupled

  implicit val cardCompleteWrites: Writes[(Card, Match, Team, Player, TypCard)] = (
    (__ \ 'card).write[Card] ~
      (__ \ 'match).write[Match] ~
      (__ \ 'team).write[Team] ~
      (__ \ 'player).write[Player] ~
      (__ \ 'typcard).write[TypCard]
    ) tupled

}