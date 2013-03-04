package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

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

  lazy val pageSize = 10

  def findAll: Seq[Event] = DB.withSession {
    implicit session => {
      (for (c <- Events.sortBy(_.name)) yield c).list
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

        val totalRows = (for (c <- Events) yield c.id).list.size
        Page(events, page, offset, totalRows)
      }
    }
  }

  def findById(id: Long): Option[Event] = DB.withSession {
    implicit session => {
      Events.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Event] = DB.withSession {
    implicit session => {
      Events.byName(name).firstOption
    }
  }

  def insert(event: Event): Long = DB.withSession {
    implicit session => {
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
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

}

