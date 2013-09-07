package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class TypMatch(id: Option[Long],
                    code: String,
                    name: String,
                    nbSubstitute: Int,
                    nbPlayer: Int,
                    periodDuration: Int,
                    hasExtraTime: Boolean,
                    extraTimeDuration: Option[Int],
                    hasInfiniteSubs: Boolean,
                    nbSubstitution: Option[Int],
                    hasPenalties: Boolean,
                    nbPenalties: Option[Int])


// define tables
object TypMatches extends Table[TypMatch]("fam_typ_match") {

  def id = column[Long]("id_typ_match", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_match")

  def code = column[String]("cod_typ_match")

  def nbSubstitute = column[Int]("nb_substitute")

  def nbPlayer = column[Int]("nb_player")

  def periodDuration = column[Int]("period_duration")

  def hasExtraTime = column[Boolean]("extra_time")

  def extraTimeDuration = column[Int]("extra_time_duration")

  def hasInfiniteSubs = column[Boolean]("infinite_subs")

  def nbSubstitution = column[Int]("nb_substitution")

  def hasPenalties = column[Boolean]("penalties")

  def nbPenalties = column[Int]("nb_penalties")

  def * = id.? ~ code ~ name ~ nbSubstitute ~ nbPlayer ~ periodDuration ~ hasExtraTime ~ extraTimeDuration.? ~ hasInfiniteSubs ~ nbSubstitution.? ~ hasPenalties ~ nbPenalties.? <>(TypMatch, TypMatch.unapply _)

  def autoInc = id.? ~ code ~ name ~ nbSubstitute ~ nbPlayer ~ periodDuration ~ hasExtraTime ~ extraTimeDuration.? ~ hasInfiniteSubs ~ nbSubstitution.? ~ hasPenalties ~ nbPenalties.? <>(TypMatch, TypMatch.unapply _) returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)

  lazy val pageSize = 10

  def findAll: Seq[TypMatch] = DB.withSession {
    implicit session:Session => {
      (for (c <- TypMatches.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(TypMatches.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[TypMatch] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session:Session =>
        val typMatchs = (
          for {c <- TypMatches
            .sortBy(typMatch => orderField match {
            case 1 => typMatch.code.asc
            case -1 => typMatch.code.desc
            case 2 => typMatch.name.asc
            case -2 => typMatch.name.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(typMatchs, page, offset, count)
    }
  }

  def findById(id: Long): Option[TypMatch] = DB.withSession {
    implicit session:Session => {
      TypMatches.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[TypMatch] = DB.withSession {
    implicit session:Session => {
      TypMatches.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[TypMatch] = DB.withSession {
    implicit session:Session => {
      TypMatches.byCode(code).firstOption
    }
  }

  def insert(typMatch: TypMatch): Long = DB.withSession {
    implicit session:Session => {
      TypMatches.autoInc.insert((typMatch))
    }
  }

  def update(id: Long, typMatch: TypMatch) = DB.withSession {
    implicit session:Session => {
      val typMatch2update = typMatch.copy(Some(id), typMatch.code, typMatch.name)
      TypMatches.where(_.id === id).update(typMatch2update)
    }
  }

  def delete(typMatchId: Long) = DB.withSession {
    implicit session:Session => {
      TypMatches.where(_.id === typMatchId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {
//    c <- findAll
//  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- TypMatches
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val typMatchFormat = Json.format[TypMatch]

}

