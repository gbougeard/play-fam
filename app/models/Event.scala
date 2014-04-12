package models

import play.api.Play.current

import play.Logger

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import org.joda.time.DateTime
import com.github.tototoshi.slick.MySQLJodaSupport._


case class Event(id: Option[Long],
                 dtEvent: DateTime,
                 duration: Int,
                 name: String,
                 typEventId: Long,
                 placeId: Option[Long],
                 eventStatusId: Long,
                 comments: Option[String])

object Events extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[Event] =  DB.withSession {
    implicit session =>
      (for {c <- events.sortBy(_.name)
      //            t <- c.typEvent
      } yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      events.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Event, TypEvent, EventStatus)] = DB.withSession {
    implicit session =>

    val offset = pageSize * page
        val q = (for {
          c <- events
          t <- c.typEvent
          e <- c.eventStatus
        } yield (c, t, e)
          ).sortBy(orderField match {
          case 1 => _._1.dtEvent.asc
          case -1 => _._1.dtEvent.desc
          case 2 => _._1.name.asc
          case -2 => _._1.name.desc
          case 3 => _._2.name.asc
          case -3 => _._2.name.desc
          case 4 => _._3.name.asc
          case -4 => _._3.name.desc
        }).drop(offset)
          .take(pageSize)

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[(Event, TypEvent, EventStatus)] = DB.withSession {
    implicit session =>
      val query = for {e <- events
                       if e.id === id
                       te <- e.typEvent
                       es <- e.eventStatus
      } yield (e, te, es)
      query.firstOption
  }

  def findByName(name: String): Option[Event] =  DB.withSession {
    implicit session =>
      events.where(_.name === name).firstOption
  }

  def insert(event: Event): Long =  DB.withSession {
    implicit session =>
      events.insert(event)
  }

  def update(id: Long, event: Event) =  DB.withSession {
    implicit session =>
      val event2update = event.copy(Some(id))
      events.where(_.id === id).update(event2update)
  }

  def delete(eventId: Long) =  DB.withSession {
    implicit session =>
      events.where(_.id === eventId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- events
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}

