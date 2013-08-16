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
                 comments:Option[String])

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

  def findPage(page: Int = 0, orderField: Int): Page[Event] = {

    val offset = pageSize * page
    DB.withSession {
      implicit session => {
        val events = (
          for {c <- Events
            .sortBy(event => orderField match {
            case 1 => event.dtEvent.asc
            case -1 => event.dtEvent.desc
            case 2 => event.name.asc
            case -2 => event.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(events, page, offset, count)
      }
    }
  }

  def findById(id: Long): Option[(Event, TypEvent, EventStatus)] = DB.withSession {
    implicit session => {
      val query = (
        for {e <- Events
             if e.id === id
             te <- e.typEvent
             es <- e.eventStatus
        } yield (e, te, es))
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
      Events.autoInc.insert((event))
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

  implicit val eventFormat = Json.format[Event]

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

