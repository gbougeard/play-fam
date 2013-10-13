package models

import play.api.Play.current

import play.Logger

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._

import models.TypEvent._
import models.EventStatus._
import database.Events

case class Event(id: Option[Long],
                 dtEvent: DateTime,
                 duration: Int,
                 name: String,
                 typEventId: Long,
                 placeId: Option[Long],
                 eventStatusId: Long,
                 comments: Option[String])

object Event{
  lazy val pageSize = 10

  def findAll: Seq[Event] = DB.withSession {
    implicit session:Session => {
      (for {c <- Events.sortBy(_.name)
      //            t <- c.typEvent
      } yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(Events.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Event, TypEvent, EventStatus)] = {

    val offset = pageSize * page
    DB.withSession {
      implicit session:Session => {
        val events = (for {
          c <- Events
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

        Page(events.list, page, offset, count)
      }
    }
  }

  def findById(id: Long): Option[(Event, TypEvent, EventStatus)] = DB.withSession {
    implicit session:Session => {
      val query = for {e <- Events
                       if e.id === id
                       te <- e.typEvent
                       es <- e.eventStatus
      } yield (e, te, es)
      query.firstOption
    }
  }

  def findByName(name: String): Option[Event] = DB.withSession {
    implicit session:Session => {
      Events.byName(name).firstOption
    }
  }

  def insert(event: Event): Long = DB.withSession {
    implicit session:Session => {
      Logger.debug("insert %s".format(event))
      Events.autoInc.insert(event)
    }
  }

  def update(id: Long, event: Event) = DB.withSession {
    implicit session:Session => {
      val event2update = event.copy(Some(id))
      Events.where(_.id === id).update(event2update)
    }
  }

  def delete(eventId: Long) = DB.withSession {
    implicit session:Session => {
      Events.where(_.id === eventId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- Events
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }


  //JSON

  val pattern = "yyyy-MM-dd'T'HH:mm:ss.sssZ"
  implicit val dateFormat = Format[DateTime](Reads.jodaDateReads(pattern), Writes.jodaDateWrites(pattern))

  implicit val eventReads: Reads[Event] = (
    (__ \ "id").readNullable[Long] ~
      (__ \ "dtEvent").read[DateTime] ~
      (__ \ "duration").read[Int] ~
      (__ \ "name").read[String] ~
      (__ \ "typEventId").read[Long] ~
      (__ \ "placeId").read[Option[Long]] ~
      (__ \ "eventStatusId").read[Long] ~
      (__ \ "comments").read[Option[String]]
    )(Event.apply _)

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val eventWrites: Writes[Event] = (
    (__ \ "id").write[Option[Long]] ~
      (__ \ "dtEvent").write[DateTime] ~
      (__ \ "duration").write[Int] ~
      (__ \ "name").write[String] ~
      (__ \ "typEventId").write[Long] ~
      (__ \ "placeId").write[Option[Long]] ~
      (__ \ "eventStatusId").write[Long] ~
      (__ \ "comments").write[Option[String]]
    )(unlift(Event.unapply))

  implicit val eventFormat = Format(eventReads, eventWrites)

  implicit val eventCompleteReads: Reads[(Event, TypEvent, EventStatus)] = (
    (__ \ 'event).read[Event] ~
      (__ \ 'typevent).read[TypEvent] ~
      (__ \ 'eventstatus).read[EventStatus]
    ) tupled

  implicit val eventCompleteWrites: Writes[(Event, TypEvent, EventStatus)] = (
    (__ \ 'event).write[Event] ~
      (__ \ 'typevent).write[TypEvent] ~
      (__ \ 'eventstatus).write[EventStatus]
    ) tupled

}

