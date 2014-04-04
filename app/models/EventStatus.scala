package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

import database.eventStatuses

case class EventStatus(id: Option[Long],
                          code: String,
                          name: String)

object EventStatuses extends DAO{
  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[EventStatus] =  {
      (for (c <- eventStatuses.sortBy(_.name)) yield c).list
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[EventStatus] = {

    val offset = pageSize * page

        val q = (
          for {c <- eventStatuses
            .sortBy(eventStatus => orderField match {
            case 1 => eventStatus.code.asc
            case -1 => eventStatus.code.desc
            case 2 => eventStatus.name.asc
            case -2 => eventStatus.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        val totalRows = eventStatuses.length.run
        Page(q, page, offset, totalRows)
  }

  def findById(id: Long)(implicit session: Session): Option[EventStatus] =  {
      eventStatuses.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[EventStatus] =  {
      eventStatuses.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[EventStatus] =  {
      eventStatuses.where(_.code === code).firstOption
  }

  def insert(eventStatus: EventStatus)(implicit session: Session): Long =  {
      eventStatuses.insert(eventStatus)
  }

  def update(id: Long, eventStatus: EventStatus)(implicit session: Session) =  {
      val eventStatus2update = eventStatus.copy(Some(id), eventStatus.code, eventStatus.name)
      eventStatuses.where(_.id === id).update(eventStatus2update)
  }

  def delete(eventStatusId: Long)(implicit session: Session) =  {
      eventStatuses.where(_.id === eventStatusId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- eventStatuses
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val eventStatusFormat = Json.format[EventStatus]

}

