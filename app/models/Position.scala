package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.Positions

case class Position(id: Option[Long],
                          code: String,
                          name: String)

object Positions extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[Position] =  DB.withSession {
    implicit session =>
      (for (c <- positions.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      positions.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[Position] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val positions = (
          for {c <- positions
            .sortBy(position => orderField match {
            case 1 => position.code.asc
            case -1 => position.code.desc
            case 2 => position.name.asc
            case -2 => position.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(positions, page, offset, count)
  }

  def findById(id: Long): Option[Position] =  DB.withSession {
    implicit session =>
      positions.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[Position] =  DB.withSession {
    implicit session =>
      positions.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[Position] =  DB.withSession {
    implicit session =>
      positions.where(_.code === code).firstOption
  }

  def insert(position: Position): Long =  DB.withSession {
    implicit session =>
      positions.insert(position)
  }

  def update(id: Long, position: Position) =  DB.withSession {
    implicit session =>
      val position2update = position.copy(Some(id), position.code, position.name)
      positions.where(_.id === id).update(position2update)
  }

  def delete(positionId: Long) =  DB.withSession {
    implicit session =>
      positions.where(_.id === positionId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- positions
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val positionFormat = Json.format[Position]

}

