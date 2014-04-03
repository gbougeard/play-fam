package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger
import database.Matches

case class Match(id: Option[Long],
                 fixtureId: Option[Long],
                 competitionId: Long,
                 eventId: Option[Long])

object Match{

  lazy val pageSize = 10

  def findAll: Seq[Match] =  {
    implicit session:Session => {
      (for (c <- Matches) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(Matches.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Match, SeasonCompetition, Event)] = {

    val offset = pageSize * page

     {
      implicit session:Session => {
        val matchs = (
          for {m <- Matches
               if m.eventId.isNotNull
               c <- m.competition
               e <- m.event
          } yield (m, c, e))
          .sortBy(orderField match {
          case 1 => _._1.id.asc
          case -1 => _._1.id.desc
          case 2 => _._3.name.asc
          case -2 => _._3.name.desc
          case 3 => _._3.dtEvent.asc
          case -3 => _._3.dtEvent.desc
        })
          .drop(offset)
          .take(pageSize)

        Page(matchs.list, page, offset, count)
      }
    }
  }

  def findById(id: Long): Option[Match] =  {
    implicit session:Session => {
      Matches.byId(id).firstOption
    }
  }

  def findByEventId(id: Long): Option[Match] =  {
    implicit session:Session => {
      Matches.byEventId(id).firstOption
    }
  }

  def insert(m: Match): Long =  {
    implicit session:Session => {
      Matches.autoInc.insert(m)
    }
  }

  def update(id: Long, m: Match) =  {
    implicit session:Session => {
      val match2update = m.copy(Some(id))
      Matches.where(_.id === id).update(match2update)
    }
  }

  def delete(matchId: Long) =  {
    implicit session:Session => {
      Matches.where(_.id === matchId).delete
    }
  }

  implicit val matchFormat = Json.format[Match]

}