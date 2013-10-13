package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.Categories

case class Category(id: Option[Long],
                          code: String,
                          name: String)


object Category{
  
  lazy val pageSize = 10

  def findAll: Seq[Category] = DB.withSession {
    implicit session:Session => {
      (for (c <- Categories.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(Categories.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Category] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session:Session =>
        val categorys = (
          for {c <- Categories
            .sortBy(category => orderField match {
            case 1 => category.code.asc
            case -1 => category.code.desc
            case 2 => category.name.asc
            case -2 => category.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(categorys, page, offset, count)
    }
  }

  def findById(id: Long): Option[Category] = DB.withSession {
    implicit session:Session => {
      Categories.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Category] = DB.withSession {
    implicit session:Session => {
      Categories.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Category] = DB.withSession {
    implicit session:Session => {
      Categories.byCode(code).firstOption
    }
  }

  def insert(category: Category): Long = DB.withSession {
    implicit session:Session => {
      Categories.autoInc.insert(category)
    }
  }

  def update(id: Long, category: Category) = DB.withSession {
    implicit session:Session => {
      val category2update = category.copy(Some(id), category.code, category.name)
      Categories.where(_.id === id).update(category2update)
    }
  }

  def delete(categoryId: Long) = DB.withSession {
    implicit session:Session => {
      Categories.where(_.id === categoryId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- Categories
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val categoryFormat = Json.format[Category]

}

