package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.Scales

case class Scale(id: Option[Long],
                 code: String,
                 name: String,
                 ptsDefeat: Int,
                 ptsDraw: Int,
                 ptsVictory: Int)


object Scales extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[Scale] =  DB.withSession {
    implicit session =>
      (for (c <- scales.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      scales.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[Scale] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val scales = (
          for {c <- scales
            .sortBy(scale => orderField match {
            case 1 => scale.code.asc
            case -1 => scale.code.desc
            case 2 => scale.name.asc
            case -2 => scale.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(scales, page, offset, count)
  }

  def findById(id: Long): Option[Scale] =  DB.withSession {
    implicit session =>
      scales.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[Scale] =  DB.withSession {
    implicit session =>
      scales.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[Scale] =  DB.withSession {
    implicit session =>
      scales.where(_.code === code).firstOption
  }

  def insert(scale: Scale): Long =  DB.withSession {
    implicit session =>
      scales.insert(scale)
  }

  def update(id: Long, scale: Scale) =  DB.withSession {
    implicit session =>
      val scale2update = scale.copy(Some(id), scale.code, scale.name)
      scales.where(_.id === id).update(scale2update)
  }

  def delete(scaleId: Long) =  DB.withSession {
    implicit session =>
      scales.where(_.id === scaleId).delete
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
        item <- scales
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val scaleFormat = Json.format[Scale]

}

