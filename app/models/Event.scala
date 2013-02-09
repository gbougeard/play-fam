package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._


// Use the implicit threadLocalSession

import scala.slick.session.Database.threadLocalSession

import org.joda.time.DateTime
import java.sql.Timestamp

case class Event(id: Option[Long],
                 dtEvent: DateTime,
                 duration: Int,
                 name: String)

// define tables
object Events extends Table[Event]("fam_event") {

  def id = column[Long]("id_event", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_event")

  def duration = column[Int]("duration")

  def dtEvent = column[DateTime]("dt_event")

  implicit val dateTime: TypeMapper[DateTime]
  = MappedTypeMapper.base[DateTime, Timestamp](dt => new
      Timestamp(dt.getMillis), ts => new DateTime(ts.getTime))

  def * = id.? ~ dtEvent ~ duration ~ name <>(Event, Event.unapply _)

  def autoInc = id.? ~ dtEvent ~ duration ~ name <>(Event, Event.unapply _) returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)

  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val pageSize = 10

  def findAll: Seq[Event] = {
    (for (c <- Events.sortBy(_.name)) yield c).list
  }

  def findPage(page: Int = 0, orderField: Int): Page[Event] = {

    val offset = pageSize * page
    database withSession {
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

      val totalRows = (for (c <- Events) yield c.id).list.size
      Page(events, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[Event] = database withSession {
    Events.byId(id).firstOption
  }

  def findByName(name: String): Option[Event] = database withSession {
    Events.byName(name).firstOption
  }

  def insert(event: Event): Long = database withSession {
    Events.autoInc.insert((event))
  }

  def update(id: Long, event: Event) = database withSession {
    val event2update = event.copy(Some(id))
    Events.where(_.id === id).update(event2update)
  }

  def delete(eventId: Long) = database withSession {
    Events.where(_.id === eventId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

}

