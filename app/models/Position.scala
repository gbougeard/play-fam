package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class Position(id: Option[Long],
                          code: String,
                          name: String)


// define tables
object Positions extends Table[Position]("fam_position") {

  def id = column[Long]("id_position", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_position")

  def code = column[String]("cod_position")

  def * = id.? ~ code ~ name <>(Position, Position.unapply _)

  def autoInc = id.? ~ code ~ name <>(Position, Position.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
//  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

  lazy val pageSize = 10

  def findAll: Seq[Position] = DB.withSession {
    implicit session => {
      (for (c <- Positions.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      Query(Positions.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Position] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
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

  def findById(id: Long): Option[Position] = DB.withSession {
    implicit session => {
      Positions.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Position] = DB.withSession {
    implicit session => {
      Positions.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Position] = DB.withSession {
    implicit session => {
      Positions.byCode(code).firstOption
    }
  }

  def insert(position: Position): Long = DB.withSession {
    implicit session => {
      Positions.autoInc.insert((position))
    }
  }

  def update(id: Long, position: Position) = DB.withSession {
    implicit session => {
      val position2update = position.copy(Some(id), position.code, position.name)
      Positions.where(_.id === id).update(position2update)
    }
  }

  def delete(positionId: Long) = DB.withSession {
    implicit session => {
      Positions.where(_.id === positionId).delete
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
        item <- Positions
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val positionFormat = Json.format[Position]

}

