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

  def findAll(implicit session: Session): Seq[Position] =  {
      (for (c <- positions.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      positions.length.run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Position] = {

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

  def findById(id: Long)(implicit session: Session): Option[Position] =  {
      positions.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[Position] =  {
      positions.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[Position] =  {
      positions.where(_.code === code).firstOption
  }

  def insert(position: Position)(implicit session: Session): Long =  {
      positions.insert(position)
  }

  def update(id: Long, position: Position)(implicit session: Session) =  {
      val position2update = position.copy(Some(id), position.code, position.name)
      positions.where(_.id === id).update(position2update)
  }

  def delete(positionId: Long)(implicit session: Session) =  {
      positions.where(_.id === positionId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- positions
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val positionFormat = Json.format[Position]

}

