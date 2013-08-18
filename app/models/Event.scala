package models

import play.api.Play.current

import play.Logger

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.joda.time.DateTime
import com.github.tototoshi.slick.JodaSupport._

import models.TypEvents._
import models.EventStatuses._

case class Event(id: Option[Long],
                 dtEvent: DateTime,
                 duration: Int,
                 name: String,
                 typEventId: Long,
                 placeId: Option[Long],
                 eventStatusId: Long,
                 comments: Option[String])

// define tables
object Events extends Table[Event]("fam_event") {

  def id = column[Long]("id_event", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_event")

  def duration = column[Int]("duration")

  def dtEvent = column[DateTime]("dt_event")

  def typEventId = column[Long]("id_typ_event")

  def placeId = column[Long]("id_place")

  def eventStatusId = column[Long]("id_eventStatus")

  def comments = column[String]("comments")

  //  implicit val dateTime: TypeMapper[DateTime] = MappedTypeMapper.base[DateTime, Timestamp](
  //    dt => new Timestamp(dt.getMillis),
  //    ts => new DateTime(ts.getTime))

  def * = id.? ~ dtEvent ~ duration ~ name ~ typEventId ~ placeId.? ~ eventStatusId ~ comments.? <>(Event, Event.unapply _)

  def autoInc = id.? ~ dtEvent ~ duration ~ name ~ typEventId ~ placeId.? ~ eventStatusId ~ comments.? <>(Event, Event.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def typEvent = foreignKey("TYP_EVENT_FK", typEventId, TypEvents)(_.id)

  def place = foreignKey("PLACE_FK", placeId, Places)(_.id)

  def eventStatus = foreignKey("EVENT_STATUS_FK", eventStatusId, EventStatuses)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)

  lazy val pageSize = 10

  def findAll: Seq[Event] = DB.withSession {
    implicit session => {
      (for {c <- Events.sortBy(_.name)
      //            t <- c.typEvent
      } yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      Query(Events.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Event, TypEvent, EventStatus)] = {

    val offset = pageSize * page
    DB.withSession {
      implicit session => {
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
    implicit session => {
      val query = for {e <- Events
                       if e.id === id
                       te <- e.typEvent
                       es <- e.eventStatus
      } yield (e, te, es)
      query.firstOption
    }
  }

  def findByName(name: String): Option[Event] = DB.withSession {
    implicit session => {
      Events.byName(name).firstOption
    }
  }

  def insert(event: Event): Long = DB.withSession {
    implicit session => {
      Logger.debug("insert %s".format(event))
      Events.autoInc.insert(event)
    }
  }

  def update(id: Long, event: Event) = DB.withSession {
    implicit session => {
      val event2update = event.copy(Some(id))
      Events.where(_.id === id).update(event2update)
    }
  }

  def delete(eventId: Long) = DB.withSession {
    implicit session => {
      Events.where(_.id === eventId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Events
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }


  //JSON
  implicit val eventReads: Reads[Event] = (
    (__ \ "id").readNullable[Long] ~
      (__ \ "dtEvent").read[DateTime].orElse(Reads.pure(new DateTime())) ~
      (__ \ "duration").read[Int] ~
      (__ \ "name").read[String] ~
      (__ \ "typEventId").read[Long] ~
      (__ \ "placeId").read[Option[Long]] ~
      (__ \ "eventStatusId").read[Long] ~
      (__ \ "comments").read[Option[String]]
    )(Event)

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

