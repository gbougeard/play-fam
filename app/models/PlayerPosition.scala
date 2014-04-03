package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger
import database.PlayerPositions

case class PlayerPosition(playerId: Long,
                        positionId: Long,
                        numOrder: Int)

object PlayerPosition{

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

  def findByPosition(id: Long): Seq[(PlayerPosition, Player, Position)] =  {
    implicit session:Session => {
      val query = for {ps <- PlayerPositions
                       if ps.positionId === id
                       p <- ps.player
                       s <- ps.position

      } yield (ps, p, s)
      query.list
    }
  }

  def findByPlayer(id: Long): Seq[(PlayerPosition, Player, Position)] =  {
    implicit session:Session => {
      val query = for {ps <- PlayerPositions
                       if ps.playerId === id
                       p <- ps.player
                       s <- ps.position

      } yield (ps, p, s)
      query.list
    }
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

//  implicit val playerPositionFormat = Json.format[PlayerPosition]
}