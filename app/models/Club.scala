package models


import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Club(id: Option[Long] = None,
                code: Int,
                name: String,
                countryId: Option[Long] = None,
                cityId: Option[Long] = None,
                colours: Option[String] = None,
                address: Option[String] = None,
                zipcode: Option[String] = None,
                city: Option[String] = None,
                organization: Option[Long] = None,
                comments: Option[String] = None
                 )


// define tables
object Clubs extends Table[Club]("fam_club") {

  def id = column[Long]("id_club", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_club")

  def code = column[Int]("code_fff")

  def countryId = column[Long]("id_country")

  def cityId = column[Long]("id_city")

  def colours = column[String]("colours")

  def address = column[String]("address")

  def zipcode = column[String]("zipcode")

  def city = column[String]("city")

  def organization = column[Long]("id_organization")

  def comments = column[String]("comments")

  def * = id.? ~ code ~ name ~ countryId.? ~ cityId.? ~ colours.? ~ address.? ~ zipcode.? ~ city.? ~ organization.? ~ comments.? <>(Club, Club.unapply _)

  def autoInc = * returning id


  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byZipCode = createFinderBy(_.zipcode)
  val byCity = createFinderBy(_.city)

  lazy val pageSize = 10

  def findAll: Seq[Club] = DB.withSession {
    implicit session: Session => {
      (for (c <- Clubs.sortBy(_.name)) yield c).list
    }
  }

  def count(filter:String): Int = DB.withSession {
    implicit session: Session => {
      Query(Clubs.where(_.name like s"%$filter%").length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int, filter: String): Page[Club] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session: Session =>
        val clubs = (
          for {c <- Clubs
               if  c.name like s"%$filter%"
          } yield c)
          .sortBy(club => orderField match {
          case 1 => club.code.asc
          case -1 => club.code.desc
          case 2 => club.name.asc
          case -2 => club.name.desc
          case 3 => club.city.asc
          case -3 => club.city.desc
          case 4 => club.zipcode.asc
          case -4 => club.zipcode.desc
        })
          .drop(offset)
          .take(pageSize)
          .list

        Page(clubs, page, offset, count(filter))
    }
  }

  def findById(id: Long): Option[Club] = DB.withSession {
    implicit session: Session => {
      Clubs.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[Club] = DB.withSession {
    implicit session: Session => {
      Clubs.byName(name).firstOption
    }
  }

  def findByCode(code: Int): Option[Club] = DB.withSession {
    implicit session: Session => {
      Clubs.byCode(code).firstOption
    }
  }

  def findByZipCode(code: String): Seq[Club] = DB.withSession {
    implicit session: Session => {
      Clubs.byZipCode(code).list
    }
  }

  def findByCity(city: String): Seq[Club] = DB.withSession {
    implicit session: Session => {
      Clubs.byCity(city).list
    }
  }

  def findLikeCity(c: String): Seq[Club] = DB.withSession {
    implicit session: Session => {
      Query(Clubs).where(_.city like s"%$c%").list
    }
  }

  def insert(club: Club): Long = DB.withSession {
    implicit session: Session => {
      Clubs.autoInc.insert((club))
    }
  }

  def update(id: Long, club: Club) = DB.withSession {
    implicit session: Session => {
      val club2update = club.copy(Some(id), club.code, club.name)
      Clubs.where(_.id === id).update(club2update)
    }
  }

  def delete(clubId: Long) = DB.withSession {
    implicit session: Session => {
      Clubs.where(_.id === clubId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {
  //    c <- findAll
  //  } yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session: Session =>
      val query = (for {
        item <- Clubs
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val clubFormat = Json.format[Club]

}

