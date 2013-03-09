package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class TypCompetition(id: Option[Long],
                          code: String,
                          name: String,
                          isChampionship: Boolean,
                          typMatchId: Long)


// define tables
object TypCompetitions extends Table[TypCompetition]("fam_typ_competition") {

  def id = column[Long]("id_typ_competition", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_competition")

  def code = column[String]("cod_typ_competition")

  def isChampionship = column[Boolean]("championship")

  def typMatchId = column[Long]("id_typ_match")

  def * = id.? ~ code ~ name ~ isChampionship ~ typMatchId <>(TypCompetition, TypCompetition.unapply _)

  def autoInc = id.? ~ code ~ name ~ isChampionship ~ typMatchId<>(TypCompetition, TypCompetition.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

  lazy val pageSize = 10

  def findAll: Seq[TypCompetition] = DB.withSession {
    implicit session => {
      (for (c <- TypCompetitions.sortBy(_.name)) yield c).list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypCompetition] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
        val typCompetitions = (
          for {c <- TypCompetitions
            .sortBy(typCompetition => orderField match {
            case 1 => typCompetition.code.asc
            case -1 => typCompetition.code.desc
            case 2 => typCompetition.name.asc
            case -2 => typCompetition.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        val totalRows = (for (c <- TypCompetitions) yield c.id).list.size
        Page(typCompetitions, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[TypCompetition] = DB.withSession {
    implicit session => {
      TypCompetitions.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[TypCompetition] = DB.withSession {
    implicit session => {
      TypCompetitions.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[TypCompetition] = DB.withSession {
    implicit session => {
      TypCompetitions.byCode(code).firstOption
    }
  }

  def insert(typCompetition: TypCompetition): Long = DB.withSession {
    implicit session => {
      TypCompetitions.autoInc.insert((typCompetition))
    }
  }

  def update(id: Long, typCompetition: TypCompetition) = DB.withSession {
    implicit session => {
      val typCompetition2update = typCompetition.copy(Some(id), typCompetition.code, typCompetition.name)
      TypCompetitions.where(_.id === id).update(typCompetition2update)
    }
  }

  def delete(typCompetitionId: Long) = DB.withSession {
    implicit session => {
      TypCompetitions.where(_.id === typCompetitionId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {
    c <- findAll
  } yield (c.id.toString, c.name)

  implicit val typCompetitionFormat = Json.format[TypCompetition]

}

