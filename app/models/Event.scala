package models

import common.Profile

import org.joda.time.DateTime
import java.sql.Timestamp

case class Event(id: Option[Long],
                dtEvent : DateTime,
                duration: Int,
                name: String)

trait EventComponent {
  this: Profile =>

  import profile.simple._


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

    def findAll(implicit session: Session): Seq[Event] = {
      (for (c <- Events.sortBy(_.name)) yield c).list
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Event] = {

      val offset = pageSize * page
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

    def findById(id: Long)(implicit session: Session): Option[Event] = {
      Events.byId(id).firstOption
    }

    def findByName(name: String)(implicit session: Session): Option[Event] = {
      Events.byName(name).firstOption
    }

    def insert(event: Event)(implicit session: Session): Long = {
      Events.autoInc.insert((event))
    }

    def update(event: Event)(implicit session: Session) = {
      Events.where(_.id === event.id).update(event)
    }

    def delete(eventId: Long)(implicit session: Session) = {
      Events.where(_.id === eventId).delete
    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options(implicit session: Session): Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  }

}

