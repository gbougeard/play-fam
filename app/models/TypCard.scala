package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class TypCard(id: Option[Long],
                          code: String,
                          name: String)


// define tables
object TypCards extends Table[TypCard]("fam_typ_card") {

  def id = column[Long]("id_typ_card", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_card")

  def code = column[String]("cod_typ_card")

  def * = id.? ~ code ~ name <>(TypCard, TypCard.unapply _)

  def autoInc = id.? ~ code ~ name <>(TypCard, TypCard.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
//  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

  lazy val pageSize = 10

  def findAll: Seq[TypCard] = DB.withSession {
    implicit session => {
      (for (c <- TypCards.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session => {
      Query(TypCards.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypCard] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
        val typCards = (
          for {c <- TypCards
            .sortBy(typCard => orderField match {
            case 1 => typCard.code.asc
            case -1 => typCard.code.desc
            case 2 => typCard.name.asc
            case -2 => typCard.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(typCards, page, offset, count)
    }
  }

  def findById(id: Long): Option[TypCard] = DB.withSession {
    implicit session => {
      TypCards.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[TypCard] = DB.withSession {
    implicit session => {
      TypCards.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[TypCard] = DB.withSession {
    implicit session => {
      TypCards.byCode(code).firstOption
    }
  }

  def insert(typCard: TypCard): Long = DB.withSession {
    implicit session => {
      TypCards.autoInc.insert((typCard))
    }
  }

  def update(id: Long, typCard: TypCard) = DB.withSession {
    implicit session => {
      val typCard2update = typCard.copy(Some(id), typCard.code, typCard.name)
      TypCards.where(_.id === id).update(typCard2update)
    }
  }

  def delete(typCardId: Long) = DB.withSession {
    implicit session => {
      TypCards.where(_.id === typCardId).delete
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
        item <- TypCards
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typCardFormat = Json.format[TypCard]

}

