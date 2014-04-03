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


object Scale{
  lazy val pageSize = 10

  def findAll: Seq[Scale] =  {
    implicit session:Session => {
      (for (c <- Scales.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(Scales.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Scale] = {

    val offset = pageSize * page

     {
      implicit session:Session =>
        val scales = (
          for {c <- Scales
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
  }

  def findById(id: Long): Option[Scale] =  {
    implicit session:Session => {
      Scales.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Scale] =  {
    implicit session:Session => {
      Scales.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Scale] =  {
    implicit session:Session => {
      Scales.byCode(code).firstOption
    }
  }

  def insert(scale: Scale): Long =  {
    implicit session:Session => {
      Scales.autoInc.insert(scale)
    }
  }

  def update(id: Long, scale: Scale) =  {
    implicit session:Session => {
      val scale2update = scale.copy(Some(id), scale.code, scale.name)
      Scales.where(_.id === id).update(scale2update)
    }
  }

  def delete(scaleId: Long) =  {
    implicit session:Session => {
      Scales.where(_.id === scaleId).delete
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
        item <- Scales
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val scaleFormat = Json.format[Scale]

}

