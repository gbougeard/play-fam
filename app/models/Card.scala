package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.Logger


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

}