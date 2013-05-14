package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class Scale(id: Option[Long],
                 code: String,
                 name: String,
                 ptsDefeat: Int,
                 ptsDraw: Int,
                 ptsVictory: Int)


// define tables
object Scales extends Table[Scale]("fam_scale") {

  def id = column[Long]("id_scale", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_scale")

  def code = column[String]("cod_scale")

  def ptsDefeat = column[Int]("pts_defeat")

  def ptsDraw = column[Int]("pts_draw")

  def ptsVictory = column[Int]("pts_victory")


  def * = id.? ~ code ~ name ~ ptsDefeat ~ ptsDraw ~ ptsVictory <>(Scale, Scale.unapply _)

  def autoInc = id.? ~ code ~ name ~ ptsDefeat ~ ptsDraw ~ ptsVictory <>(Scale, Scale.unapply _) returning id


  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

  lazy val pageSize = 10

  def findAll: Seq[Scale] = DB.withSession {
    implicit session => {
      (for (c <- Scales.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      Query(Scales.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Scale] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
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

  def findById(id: Long): Option[Scale] = DB.withSession {
    implicit session => {
      Scales.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Scale] = DB.withSession {
    implicit session => {
      Scales.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Scale] = DB.withSession {
    implicit session => {
      Scales.byCode(code).firstOption
    }
  }

  def insert(scale: Scale): Long = DB.withSession {
    implicit session => {
      Scales.autoInc.insert((scale))
    }
  }

  def update(id: Long, scale: Scale) = DB.withSession {
    implicit session => {
      val scale2update = scale.copy(Some(id), scale.code, scale.name)
      Scales.where(_.id === id).update(scale2update)
    }
  }

  def delete(scaleId: Long) = DB.withSession {
    implicit session => {
      Scales.where(_.id === scaleId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session =>
      val query = (for {
        item <- Scales
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val scaleFormat = Json.format[Scale]

}

