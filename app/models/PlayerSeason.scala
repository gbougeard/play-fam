package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger
import database.PlayerSeasons

case class PlayerSeason(playerId: Long,
                        seasonId: Long,
                        teamId: Option[Long],
                        clubId: Long,
                        cap_att: Option[Long],
                        cap_def: Option[Long],
                        cap_phy: Option[Long],
                        cap_pui: Option[Long],
                        cap_tec: Option[Long],
                        cap_vit: Option[Long],
                        height: Option[Double],
                        weight: Option[Double],
                        statAvgAssistPerMatch: Option[Double],
                        statAvgGoalPerMatch: Option[Double],
                        statAvgNote: Option[Double],
                        statNbAssist: Option[Long],
                        statNbGoal: Option[Long],
                        statNbMatch: Option[Long],
                        statNbSubstitute: Option[Long],
                        statNbWorkout: Option[Long],
                        statTimePlayed: Option[Long]
                         )

object PlayerSeasons extends DAO{
  lazy val pageSize = 10

  //  def findPage(page: Int = 0, orderField: Int): Page[(Player)] = {
  //
  //    val offset = pageSize * page
  //
  //     {
  //      implicit session:Session => {
  //        val players = (
  //          for {t <- Players
  //            .sortBy(player => orderField match {
  //            case 1 => player.firstName.asc
  //            case -1 => player.firstName.desc
  //            case 2 => player.lastName.asc
  //            case -2 => player.lastName.desc
  //            case 3 => player.email.asc
  //            case -3 => player.email.desc
  //          })
  //            .drop(offset)
  //            .take(pageSize)
  //          } yield (t)).list
  //
  //        val totalRows = (for {t <- Players} yield t.id).list.size
  //        Page(players, page, offset, totalRows)
  //      }
  //    }
  //  }

  def findBySeason(id: Long): Seq[(PlayerSeason, Player, Season, Club)] =  DB.withSession {
    implicit session =>
      val query = for {ps <- playerSeasons
                       if ps.seasonId === id
                       c <- ps.club
                       p <- ps.player
                       s <- ps.season

      } yield (ps, p, s, c)
      query.list
  }

  def findByPlayer(id: Long): Seq[(PlayerSeason, Player, Season, Team, Club)] =  DB.withSession {
    implicit session =>
      val query = for {ps <- playerSeasons
                       if ps.playerId === id
                       t <- ps.team
                       c <- ps.club
                       p <- ps.player
                       s <- ps.season

      } yield (ps, p, s, t, c)
      query.list
  }

  def findByClub(id: Long): Seq[(PlayerSeason, Player, Season, Team, Club)] =  DB.withSession {
    implicit session =>
      val query = for {ps <- playerSeasons
                       if ps.clubId === id
                       t <- ps.team
                       c <- ps.club
                       p <- ps.player
                       s <- ps.season

      } yield (ps, p, s, t, c)
      query.list
  }

  def findByClubAndSeason(idClub: Long, idSeason: Long): Seq[(PlayerSeason, Player, Season, Club)] =  DB.withSession {
    implicit session =>
      val query = for {ps <- playerSeasons
                       if ps.clubId === idClub
                       if ps.seasonId === idSeason
                       c <- ps.club
                       p <- ps.player
                       s <- ps.season

      } yield (ps, p, s, c)
      query.list
  }

  def findPlayerByClubAndSeason(idPlayer: Long, idClub: Long, idSeason: Long): Option[(PlayerSeason, Player, Season, Club)] =  DB.withSession {
    implicit session =>
      val query = for {ps <- playerSeasons
                       if ps.clubId === idClub
                       if ps.seasonId === idSeason
                       if ps.playerId === idPlayer
                       c <- ps.club
                       p <- ps.player
                       s <- ps.season

      } yield (ps, p, s, c)
      query.firstOption
  }

  def findByTeam(id: Long): Seq[(PlayerSeason, Player, Season, Team, Club)] =  DB.withSession {
    implicit session =>
      val query = for {ps <- playerSeasons
                       if ps.teamId === id
                       t <- ps.team
                       c <- ps.club
                       p <- ps.player
                       s <- ps.season

      } yield (ps, p, s, t, c)
      query.list
  }

  //  def insert(player: Player): Long =  {
  //    implicit session:Session => {
  //      Players.autoInc.insert((player))
  //    }
  //  }
  //
  //  def update(id: Long, player: Player) =  {
  //    implicit session:Session => {
  //      val player2update = player.copy(Some(id))
  //      Logger.info("playe2update " + player2update)
  //      Players.where(_.id === id).update(player2update)
  //    }
  //  }
  //
  //  def delete(playerId: Long) =  {
  //    implicit session:Session => {
  //      Players.where(_.id === playerId).delete
  //    }
  //  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)

  //  def options: Seq[(String, String)] =  {
  //    implicit session:Session =>
  //      val query = (for {
  //        item <- Players
  //      } yield (item.id, item.firstName + " " + item.lastName)
  //        ).sortBy(_._2)
  //      query.list.map(row => (row._1.toString, row._2))
  //  }

//  implicit val playerSeasonFormat = Json.format[PlayerSeason]
}