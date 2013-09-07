package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import States._

case class Province(id: Option[Long],
                    code: String,
                    name: String,
                    upper: String,
                    lower: String,
                    stateId: Long)

// define tables
object Provinces extends Table[Province]("fam_province") {

  def id = column[Long]("id_province", O.PrimaryKey, O.AutoInc)

  def code = column[String]("cod_province")

  def name = column[String]("lib_province")

  def upper = column[String]("lib_Upper")

  def lower = column[String]("lib_lower")

  def stateId = column[Long]("id_state")

  def * = id.? ~ code ~ name ~ upper ~ lower ~ stateId <>(Province, Province.unapply _)

  def autoInc = id.? ~ code ~ name ~ upper ~ lower ~ stateId <>(Province, Province.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def state = foreignKey("STATE_FK", stateId, States)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byUpper = createFinderBy(_.upper)
  val byLower = createFinderBy(_.lower)
  val byState = createFinderBy(_.stateId)

  lazy val pageSize = 10

  def findAll: Seq[Province] = DB.withSession {
    implicit session:Session => {
      (for (c <- Provinces.sortBy(_.code)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(Provinces.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(Province, State)] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session:Session => {
        val provinces = (
          for {p <- Provinces

               s <- p.state
          } yield (p, s))
          .sortBy(orderField match {
          case 1 => _._1.code asc
          case -1 => _._1.code desc
          case 2 => _._1.name asc
          case -2 => _._1.name desc
          case 3 => _._2.name asc
          case -3 => _._2.name desc
        })
          .drop(offset)
          .take(pageSize)

        val totalRows = count
        Page(provinces.list, page, offset, totalRows)
      }
    }
  }

  def findById(id: Long): Option[Province] = DB.withSession {
    implicit session:Session => {
      Provinces.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Province] = DB.withSession {
    implicit session:Session => {
      Provinces.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[Province] = DB.withSession {
    implicit session:Session => {
      Provinces.byCode(code).firstOption
    }
  }

  def insert(province: Province): Long = DB.withSession {
    implicit session:Session => {
      Provinces.autoInc.insert((province))
    }
  }

  def update(id: Long, province: Province) = DB.withSession {
    implicit session:Session => {
      Provinces.where(_.id === province.id).update(province.copy(Some(id)))
    }
  }

  def delete(provinceId: Long) = DB.withSession {
    implicit session:Session => {
      Provinces.where(_.id === provinceId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.code + " - " + c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- Provinces
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  //JSON
  implicit val provinceFormat = Json.format[Province]

  implicit val provinceWithStateReads: Reads[(Province, State)] = (
    (__ \ 'province).read[Province] ~
      (__ \ 'state).read[State]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val provinceWithStateWrites: Writes[(Province, State)] = (
    (__ \ 'province).write[Province] ~
      (__ \ 'state).write[State]
    ) tupled
  implicit val provinceWithStateFormat = Format(provinceWithStateReads, provinceWithStateWrites)

}