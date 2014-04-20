package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class Category(id: Option[Long],
                          code: String,
                          name: String)


object CategoryJson {

  import play.api.libs.json._
  import play.api.libs.functional.syntax._

  implicit val categoryJsonFormat = Json.format[Category]

}


object Categories extends DAO{
  
  lazy val pageSize = 10

  def findAll: Seq[Category] =  DB.withSession {
    implicit session =>
      (for (c <- categories.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      categories.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[Category] = DB.withSession {
    implicit session =>

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

  def findById(id: Long): Option[Category] =  DB.withSession {
    implicit session =>
      categories.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[Category] =  DB.withSession {
    implicit session =>
      categories.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[Category] =  DB.withSession {
    implicit session =>
      categories.where(_.code === code).firstOption
  }

  def insert(category: Category): Long =  DB.withSession {
    implicit session =>
      categories.insert(category)
  }

  def update(id: Long, category: Category) =  DB.withSession {
    implicit session =>
      val category2update = category.copy(Some(id), category.code, category.name)
      categories.where(_.id === id).update(category2update)
  }

  def delete(categoryId: Long) =  DB.withSession {
    implicit session =>
      categories.where(_.id === categoryId).delete
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
        item <- categories
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}

