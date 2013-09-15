package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Place(id: Option[Long] = None,
                 name: String,
                 address: String,
                 city: String,
                 zipcode: String,
                 latitude: Option[Float] = None,
                 longitude: Option[Float] = None,
comments: Option[String] = None,
typFff: Option[String] = None
                  )


// define tables
object Places extends Table[Place]("fam_place") {

  def id = column[Long]("id_place", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_place")

  def address = column[String]("address")

  def city = column[String]("city")

  def zipcode = column[String]("zipcode")

  def latitude = column[Float]("latitude")

  def longitude = column[Float]("longitude")
  def comments = column[String]("comments")
  def typFff = column[String]("typ_fff")

  def * = id.? ~ name ~ address ~ city ~ zipcode ~ latitude.? ~ longitude.? ~ comments.? ~ typFff.? <>(Place, Place.unapply _)

  def autoInc = * returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.zipcode)
  val byCity = createFinderBy(_.city)

  lazy val pageSize = 2500

  def findAll: Seq[Place] = DB.withSession {
    implicit session:Session => {
      (for (c <- Places.sortBy(_.name)) yield c).list
    }
  }

  def count: Int = DB.withSession {
    implicit session:Session => {
      Query(Places.length).first
    }
  }

  def placesWithCoords: Seq[Place] = DB.withSession {
    implicit session:Session => {
      (for {c <- Places sortBy (_.zipcode)
            if (c.latitude isNotNull)
            if (c.longitude isNotNull)
      } yield c).list
    }
  }

  def placesWithoutCoords: Seq[Place] = DB.withSession {
    implicit session:Session => {
      (for {c <- Places sortBy (_.zipcode)
            if (c.latitude isNull)
            if (c.longitude isNull)
      } yield c)
        .take(1000)
        .list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Place] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session:Session => {
        val places = (
          for {c <- Places
            .sortBy(place => orderField match {
            case 1 => place.zipcode.asc
            case -1 => place.zipcode.desc
            case 2 => place.name.asc
            case -2 => place.name.desc
            case 3 => place.address.asc
            case -3 => place.address.desc
            case 4 => place.city.asc
            case -4 => place.city.desc
            case 5 => place.zipcode.asc
            case -5 => place.zipcode.desc
            case 6 => place.typFff.asc
            case -6 => place.typFff.desc
          })
            .drop(offset)
            .take(pageSize)
          } yield c).list

        Page(places, page, offset, count)
      }
    }
  }

  def findById(id: Long): Option[Place] = DB.withSession {
    implicit session:Session => {
      Places.byId(id).firstOption
    }
  }

  def findByName(name: String): Seq[Place] = DB.withSession {
    implicit session:Session => {
      Places.byName(name).list
    }
  }

  def findByZipcode(zipcode: String): Seq[Place] = DB.withSession {
    implicit session:Session => {
      Places.byCode(zipcode).list
    }
  }

  def findByCity(city: String): Seq[Place] = DB.withSession {
    implicit session:Session => {
      Places.byCity(city).list
    }
  }

  def insert(place: Place): Long = DB.withSession {
    implicit session:Session => {
      Places.autoInc.insert((place))
    }
  }

  def update(id: Long, place: Place) = DB.withSession {
    implicit session:Session => {
      val place2update = place.copy(Some(id))
      Places.where(_.id === id).update(place2update)
    }
  }

  def delete(placeId: Long) = DB.withSession {
    implicit session:Session => {
      Places.where(_.id === placeId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session:Session =>
      val query = (for {
        item <- Places
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val placeFormat = Json.format[Place]


}

