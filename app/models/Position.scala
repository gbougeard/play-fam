package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.Positions

case class Position(id: Option[Long],
                          code: String,
                          name: String)

object Position{
  lazy val pageSize = 10

  def findAll: Seq[Position] =  {
    implicit session:Session => {
      (for (c <- Positions.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(Positions.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Position] = {

    val offset = pageSize * page

     {
      implicit session:Session =>
        val positions = (
          for {c <- Positions
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
  }

  def findById(id: Long): Option[Position] =  {
    implicit session:Session => {
      Positions.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Position] =  {
    implicit session:Session => {
      Positions.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Position] =  {
    implicit session:Session => {
      Positions.byCode(code).firstOption
    }
  }

  def insert(position: Position): Long =  {
    implicit session:Session => {
      Positions.autoInc.insert(position)
    }
  }

  def update(id: Long, position: Position) =  {
    implicit session:Session => {
      val position2update = position.copy(Some(id), position.code, position.name)
      Positions.where(_.id === id).update(position2update)
    }
  }

  def delete(positionId: Long) =  {
    implicit session:Session => {
      Positions.where(_.id === positionId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  {
    implicit session:Session =>
      val query = (for {
        item <- Positions
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val positionFormat = Json.format[Position]

}

