package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.Categories

case class Category(id: Option[Long],
                          code: String,
                          name: String)


object Categories extends DAO{
  
  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[Category] =  {
      (for (c <- categories.sortBy(_.name)) yield c).list
  }

  def count(implicit session: Session): Int =  {
      categories.length.run
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Category] = {

    val offset = pageSize * page

        val categorys = (
          for {c <- categories
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

  def findById(id: Long)(implicit session: Session): Option[Category] =  {
      categories.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Option[Category] =  {
      categories.where(_.name === name).firstOption
  }

  def findByCode(code: String)(implicit session: Session): Option[Category] =  {
      categories.where(_.code === code).firstOption
  }

  def insert(category: Category)(implicit session: Session): Long =  {
      categories.insert(category)
  }

  def update(id: Long, category: Category)(implicit session: Session) =  {
      val category2update = category.copy(Some(id), category.code, category.name)
      categories.where(_.id === id).update(category2update)
  }

  def delete(categoryId: Long)(implicit session: Session) =  {
      categories.where(_.id === categoryId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- categories
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val categoryFormat = Json.format[Category]

}

