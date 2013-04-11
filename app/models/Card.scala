package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import models.Matchs._
import models.Teams._
import models.Players._
import models.TypCards._

import play.api.Logger

case class Card(id: Option[Long],
                        matchId: Long,
                        teamId: Long,
                        playerId: Long,
                        typCardId: Long,
                        time: Option[Long])

// define tables
object Cards extends Table[Card]("fam_card") {

  def id = column[Long]("id_card")

  def matchId = column[Long]("id_match")

  def teamId = column[Long]("id_team")

  def playerId = column[Long]("id_player")

  def typCardId = column[Long]("id_typ_card")

  def time = column[Long]("card_time")

  def * = id.? ~ matchId ~ teamId ~ playerId ~ typCardId ~ time.? <>(Card, Card.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def matche = foreignKey("MATCH_FK", matchId, Matchs)(_.id)

  def player = foreignKey("PLAYER_FK", playerId, Players)(_.id)

  def typCard = foreignKey("TYP_CARD_FK", typCardId, TypCards)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

  lazy val pageSize = 10

  def findByMatchAndTeam(idMatch: Long, idTeam: Long): Seq[(Card, Match, Team, Player, TypCard)] = DB.withSession {
    implicit session => {
      val query = (
        for {mp <- Cards
             if mp.matchId === idMatch
             if mp.teamId === idTeam
             m <- mp.matche
             t <- mp.team
             p <- mp.player
             tc <- mp.typCard

        } yield (mp, m, t, p, tc))
      query.list.sortBy(_._1.time)
    }
  }

  def insert(card: Card): Long = DB.withSession {
    implicit session => {
      Cards.insert((card))
    }
  }

  def update(id: Long, card: Card) = DB.withSession {
    implicit session => {
      val card2update = card.copy(Some(id))
//      Logger.info("playe2update " + card2update)
      Cards.where(_.id === id).update(card2update)
    }
  }

  def delete(id: Long) = DB.withSession {
    implicit session => {
      Cards.where(_.id === id).delete
    }
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