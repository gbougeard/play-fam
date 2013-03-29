package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger

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

// define tables
object PlayerSeasons extends Table[PlayerSeason]("fam_player_season") {

  def playerId = column[Long]("id_player")

  def seasonId = column[Long]("id_season")

  def teamId = column[Long]("id_team")

  def clubId = column[Long]("id_club")

  def cap_att = column[Long]("att")

  def cap_def = column[Long]("def")

  def cap_phy = column[Long]("phy")

  def cap_pui = column[Long]("pui")

  def cap_tec = column[Long]("tec")

  def cap_vit = column[Long]("vit")

  def height = column[Double]("height")

  def weight = column[Double]("weight")

  def statAvgAssistPerMatch = column[Double]("avg_assist_per_match")

  def statAvgGoalPerMatch = column[Double]("avg_goal_per_match")

  def statAvgNote = column[Double]("avg_note")

  def statNbAssist = column[Long]("nb_assist")

  def statNbGoal = column[Long]("nb_goal")

  def statNbMatch = column[Long]("nb_match")

  def statNbSubstitue = column[Long]("nb_substitute")

  def statNbWorkout = column[Long]("nb_workout")

  def statTimePlayed = column[Long]("time_played")

  def * = playerId ~ seasonId ~ teamId.? ~ clubId ~ cap_att.? ~ cap_def.? ~ cap_phy.? ~ cap_pui.? ~ cap_tec.? ~ cap_vit.? ~ height.? ~ weight.? ~ statAvgAssistPerMatch.? ~ statAvgGoalPerMatch.? ~ statAvgNote.? ~ statNbAssist.? ~ statNbGoal.? ~ statNbMatch.? ~ statNbSubstitue.? ~ statNbWorkout.? ~ statTimePlayed.? <>(PlayerSeason, PlayerSeason.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def player = foreignKey("PLAYER_FK", playerId, Players)(_.id)

  def season = foreignKey("SEASON_FK", seasonId, Seasons)(_.id)

  def team = foreignKey("TEAM_FK", teamId, Teams)(_.id)

  def club = foreignKey("CLUB_FK", clubId, Clubs)(_.id)

  lazy val pageSize = 10

  //  def findPage(page: Int = 0, orderField: Int): Page[(Player)] = {
  //
  //    val offset = pageSize * page
  //
  //    DB.withSession {
  //      implicit session => {
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

  def findBySeason(id: Long): Seq[(PlayerSeason, Player, Season, Club)] = DB.withSession {
    implicit session => {
      val query = (
        for {ps <- PlayerSeasons
             if ps.seasonId === id
             c <- ps.club
             p <- ps.player
             s <- ps.season

        } yield (ps, p, s, c))
      query.list
    }
  }

  def findByPlayer(id: Long): Seq[(PlayerSeason, Player, Season, Team, Club)] = DB.withSession {
    implicit session => {
      val query = (
        for {ps <- PlayerSeasons
             if ps.playerId === id
             t <- ps.team
             c <- ps.club
             p <- ps.player
             s <- ps.season

        } yield (ps, p, s, t, c))
      query.list
    }
  }

  def findByClub(id: Long): Seq[(PlayerSeason, Player, Season, Team, Club)] = DB.withSession {
    implicit session => {
      val query = (
        for {ps <- PlayerSeasons
             if ps.clubId === id
             t <- ps.team
             c <- ps.club
             p <- ps.player
             s <- ps.season

        } yield (ps, p, s, t, c))
      query.list
    }
  }

  def findByClubAndSeason(idClub: Long, idSeason: Long): Seq[(PlayerSeason, Player, Season, Club)] = DB.withSession {
    implicit session => {
      val query = (
        for {ps <- PlayerSeasons
             if ps.clubId === idClub
             if ps.seasonId === idSeason
             c <- ps.club
             p <- ps.player
             s <- ps.season

        } yield (ps, p, s, c))
      query.list
    }
  }

  def findPlayerByClubAndSeason(idPlayer: Long, idClub: Long, idSeason: Long): Option[(PlayerSeason, Player, Season, Club)] = DB.withSession {
    implicit session => {
      val query = (
        for {ps <- PlayerSeasons
             if ps.clubId === idClub
             if ps.seasonId === idSeason
             if ps.playerId === idPlayer
             c <- ps.club
             p <- ps.player
             s <- ps.season

        } yield (ps, p, s, c))
      query.firstOption
    }
  }

  def findByTeam(id: Long): Seq[(PlayerSeason, Player, Season, Team, Club)] = DB.withSession {
    implicit session => {
      val query = (
        for {ps <- PlayerSeasons
             if ps.teamId === id
             t <- ps.team
             c <- ps.club
             p <- ps.player
             s <- ps.season

        } yield (ps, p, s, t, c))
      query.list
    }
  }

  //  def insert(player: Player): Long = DB.withSession {
  //    implicit session => {
  //      Players.autoInc.insert((player))
  //    }
  //  }
  //
  //  def update(id: Long, player: Player) = DB.withSession {
  //    implicit session => {
  //      val player2update = player.copy(Some(id))
  //      Logger.info("playe2update " + player2update)
  //      Players.where(_.id === id).update(player2update)
  //    }
  //  }
  //
  //  def delete(playerId: Long) = DB.withSession {
  //    implicit session => {
  //      Players.where(_.id === playerId).delete
  //    }
  //  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)

  //  def options: Seq[(String, String)] = DB.withSession {
  //    implicit session =>
  //      val query = (for {
  //        item <- Players
  //      } yield (item.id, item.firstName + " " + item.lastName)
  //        ).sortBy(_._2)
  //      query.list.map(row => (row._1.toString, row._2))
  //  }

//  implicit val playerSeasonFormat = Json.format[PlayerSeason]
}