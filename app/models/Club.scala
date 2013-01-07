package models

import common.Profile

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Club(id: Option[Long],
                code: Int,
                name: String)

trait ClubComponent {
  this: Profile =>

  import profile.simple._

  implicit val clubFormat = Json.format[Club]

  // define tables
  object Clubs extends Table[Club]("fam_club") {

    def id = column[Long]("id_club", O.PrimaryKey, O.AutoInc)

    def name = column[String]("lib_club")

    def code = column[Int]("code_fff")

    def * = id.? ~ code ~ name <>(Club, Club.unapply _)

    def autoInc = id.? ~ code ~ name <>(Club, Club.unapply _) returning id

    val byId = createFinderBy(_.id)
    val byName = createFinderBy(_.name)
    val byCode = createFinderBy(_.code)

    lazy val pageSize = 10

    def findAll(implicit session: Session): Seq[Club] = {
      (for (c <- Clubs.sortBy(_.name)) yield c).list
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Club] = {

      val offset = pageSize * page
      val clubs = (
        for {c <- Clubs
          .sortBy(club => orderField match {
          case 1 => club.code.asc
          case -1 => club.code.desc
          case 2 => club.name.asc
          case -2 => club.name.desc
        })
          .drop(offset)
          .take(pageSize)
        } yield c).list

      val totalRows = (for (c <- Clubs) yield c.id).list.size
      Page(clubs, page, offset, totalRows)
    }

    def findById(id: Long)(implicit session: Session): Option[Club] = {
      Clubs.byId(id).firstOption
    }

    def findByName(name: String)(implicit session: Session): Option[Club] = {
      Clubs.byName(name).firstOption
    }

    def findByCode(code: Int)(implicit session: Session): Option[Club] = {
      Clubs.byCode(code).firstOption
    }

    def insert(club: Club)(implicit session: Session): Long = {
      Clubs.autoInc.insert((club))
    }

    def update(club: Club)(implicit session: Session) = {
      Clubs.where(_.id === club.id).update(club)
    }

    def delete(clubId: Long)(implicit session: Session) = {
      Clubs.where(_.id === clubId).delete
    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options(implicit session: Session): Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  }

}

