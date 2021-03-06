package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


import play.api.Logger

case class Match(id: Option[Long],
                 fixtureId: Option[Long],
                 competitionId: Long,
                 eventId: Option[Long])

object Matches extends DAO{

  lazy val pageSize = 10

  def findAll: Seq[Match] =  DB.withSession {
    implicit session =>
      (for (c <- matches) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      matches.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Match, SeasonCompetition, Event)] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val matchs = (
          for {m <- matches
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

  def findById(id: Long): Option[Match] =  DB.withSession {
    implicit session =>
      matches.where(_.id === id).firstOption
  }

  def findByEventId(id: Long): Option[Match] =  DB.withSession {
    implicit session =>
      matches.where(_.eventId === id).firstOption
  }

  def insert(m: Match): Long =  DB.withSession {
    implicit session =>
      matches.insert(m)
  }

  def update(id: Long, m: Match) =  DB.withSession {
    implicit session =>
      val match2update = m.copy(Some(id))
      matches.where(_.id === id).update(match2update)
  }

  def delete(matchId: Long) =  DB.withSession {
    implicit session =>
      matches.where(_.id === matchId).delete
  }


}