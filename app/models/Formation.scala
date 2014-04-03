package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import database.Formations

case class Formation(id: Option[Long],
                     code: String,
                     name: String,
                     isDefault: Boolean,
                     typMatchId: Long)


object Formation{
  lazy val pageSize = 10

  def findAll: Seq[Formation] =  {
    implicit session:Session => {
      (for (c <- Formations.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(Formations.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Formation, TypMatch)] = {

    val offset = pageSize * page

     {
      implicit session:Session =>
        val formations = (
          for {c <- Formations
            .sortBy(formation => orderField match {
            case 1 => formation.code.asc
            case -1 => formation.code.desc
            case 2 => formation.name.asc
            case -2 => formation.name.desc
          })

            .drop(offset)
            .take(pageSize)
               tm <- c.typMatch
          } yield (c, tm)).list

        Page(formations, page, offset, count)
    }
  }

  def findById(id: Long): Option[Formation] =  {
    implicit session:Session => {
      Formations.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Formation] =  {
    implicit session:Session => {
      Formations.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Formation] =  {
    implicit session:Session => {
      Formations.byCode(code).firstOption
    }
  }

  def insert(formation: Formation): Long =  {
    implicit session:Session => {
      Formations.autoInc.insert(formation)
    }
  }

  def update(id: Long, formation: Formation) =  {
    implicit session:Session => {
      val formation2update = formation.copy(Some(id), formation.code, formation.name)
      Formations.where(_.id === id).update(formation2update)
    }
  }

  def delete(formationId: Long) =  {
    implicit session:Session => {
      Formations.where(_.id === formationId).delete
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
        item <- Formations
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val formationFormat = Json.format[Formation]

}

