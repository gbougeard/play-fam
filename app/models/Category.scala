package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class Category(id: Option[Long],
                          code: String,
                          name: String)


// define tables
object Categorys extends Table[Category]("fam_category") {

  def id = column[Long]("id_category", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_category")

  def code = column[String]("cod_category")


  def * = id.? ~ code ~ name  <>(Category, Category.unapply _)

  def autoInc = id.? ~ code ~ name <>(Category, Category.unapply _) returning id


  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

  lazy val pageSize = 10

  def findAll: Seq[Category] = DB.withSession {
    implicit session => {
      (for (c <- Categorys.sortBy(_.name)) yield c).list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Category] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
        val categorys = (
          for {c <- Categorys
            .sortBy(category => orderField match {
            case 1 => category.code.asc
            case -1 => category.code.desc
            case 2 => category.name.asc
            case -2 => category.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        val totalRows = (for (c <- Categorys) yield c.id).list.size
        Page(categorys, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[Category] = DB.withSession {
    implicit session => {
      Categorys.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Category] = DB.withSession {
    implicit session => {
      Categorys.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Category] = DB.withSession {
    implicit session => {
      Categorys.byCode(code).firstOption
    }
  }

  def insert(category: Category): Long = DB.withSession {
    implicit session => {
      Categorys.autoInc.insert((category))
    }
  }

  def update(id: Long, category: Category) = DB.withSession {
    implicit session => {
      val category2update = category.copy(Some(id), category.code, category.name)
      Categorys.where(_.id === id).update(category2update)
    }
  }

  def delete(categoryId: Long) = DB.withSession {
    implicit session => {
      Categorys.where(_.id === categoryId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {
    c <- findAll
  } yield (c.id.toString, c.name)

  implicit val categoryFormat = Json.format[Category]

}

