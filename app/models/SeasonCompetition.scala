package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class SeasonCompetition(id: Option[Long],
                             categoryId: Long,
                             scaleId: Long,
                             typCompetitionId: Long)


// define tables
object SeasonCompetitions extends Table[SeasonCompetition]("fam_season_competition") {

  def id = column[Long]("id_season_competition", O.PrimaryKey, O.AutoInc)

  def categoryId = column[Long]("id_category")

  def scaleId = column[Long]("id_scale")

  def typCompetitionId = column[Long]("id_typ_competition")

  def * = id.? ~ categoryId ~ scaleId ~ typCompetitionId <>(SeasonCompetition, SeasonCompetition.unapply _)

  def autoInc = id.? ~ categoryId ~ scaleId ~ typCompetitionId <>(SeasonCompetition, SeasonCompetition.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def category = foreignKey("CATEGORY_FK", categoryId, Categorys)(_.id)

  def scale = foreignKey("SCALE_FK", scaleId, Scales)(_.id)

  def typCompetition = foreignKey("TYP_COMPETITION_FK", typCompetitionId, TypCompetitions)(_.id)


  val byId = createFinderBy(_.id)
  val byTypCompetition = createFinderBy(_.typCompetitionId)
  val byCategory = createFinderBy(_.categoryId)

  lazy val pageSize = 10

  def findAll: Seq[SeasonCompetition] = DB.withSession {
    implicit session => {
      (for (c <- SeasonCompetitions.sortBy(_.typCompetitionId)) yield c).list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[SeasonCompetition] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session =>
        val seasonCompetitions = (
          for {c <- SeasonCompetitions
            .sortBy(seasonCompetition => orderField match {
            case 1 => seasonCompetition.categoryId.asc
            case -1 => seasonCompetition.categoryId.desc
            case 2 => seasonCompetition.typCompetitionId.asc
            case -2 => seasonCompetition.typCompetitionId.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        val totalRows = (for (c <- SeasonCompetitions) yield c.id).list.size
        Page(seasonCompetitions, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[SeasonCompetition] = DB.withSession {
    implicit session => {
      SeasonCompetitions.byId(id).firstOption
    }
  }

  def findByCategory(category: Long): Option[SeasonCompetition] = DB.withSession {
    implicit session => {
      SeasonCompetitions.byCategory(category).firstOption
    }
  }

  def findByTypCompetition(typCompetition: Long): Option[SeasonCompetition] = DB.withSession {
    implicit session => {
      SeasonCompetitions.byTypCompetition(typCompetition).firstOption
    }
  }

  def insert(seasonCompetition: SeasonCompetition): Long = DB.withSession {
    implicit session => {
      SeasonCompetitions.autoInc.insert((seasonCompetition))
    }
  }

  def update(id: Long, seasonCompetition: SeasonCompetition) = DB.withSession {
    implicit session => {
      val seasonCompetition2update = seasonCompetition.copy(Some(id))
      SeasonCompetitions.where(_.id === id).update(seasonCompetition2update)
    }
  }

  def delete(seasonCompetitionId: Long) = DB.withSession {
    implicit session => {
      SeasonCompetitions.where(_.id === seasonCompetitionId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {
    c <- findAll
  } yield (c.id.toString, c.typCompetitionId.toString())

  implicit val seasonCompetitionFormat = Json.format[SeasonCompetition]

}

