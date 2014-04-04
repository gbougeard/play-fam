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

object Matches extends DAO{

  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[Match] =  {
      (for (c <- matches) yield c).list
  }

  def count(implicit session: Session): Int =  {
      matches.length.run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(Match, SeasonCompetition, Event)] = {

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

  def findById(id: Long)(implicit session: Session): Option[Match] =  {
      matches.where(_.id === id).firstOption
  }

  def findByEventId(id: Long)(implicit session: Session): Option[Match] =  {
      matches.where(_.eventId === id).firstOption
  }

  def insert(m: Match)(implicit session: Session): Long =  {
      matches.insert(m)
  }

  def update(id: Long, m: Match)(implicit session: Session) =  {
      val match2update = m.copy(Some(id))
      matches.where(_.id === id).update(match2update)
  }

  def delete(matchId: Long)(implicit session: Session) =  {
      matches.where(_.id === matchId).delete
  }

  implicit val matchFormat = Json.format[Match]

}