package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Place(id: Option[Long],
                 name: String,
                 address: String,
                 city: String,
                 zipcode: Int,
                 latitude: Option[Float],
                 longitude: Option[Float]
                  )


// define tables
object Places extends Table[Place]("fam_place") {

  def id = column[Long]("id_place", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_place")

  def address = column[String]("address")

  def city = column[String]("city")

  def zipcode = column[Int]("zipcode")

  def latitude = column[Float]("latitude")

  def longitude = column[Float]("longitude")

  def * = id.? ~ name ~ address ~ city ~ zipcode ~ latitude.? ~ longitude.? <>(Place, Place.unapply _)

  def autoInc = id.? ~ name ~ address ~ city ~ zipcode ~ latitude.? ~ longitude.? <>(Place, Place.unapply _) returning id

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.zipcode)
  val byCity = createFinderBy(_.city)

  lazy val pageSize = 10

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
      (for {c <- Places sortBy (_.name)
            if (c.latitude isNotNull)
            if (c.longitude isNotNull)
      } yield c).list
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

  def findByName(name: String): Option[Place] = DB.withSession {
    implicit session:Session => {
      Places.byName(name).firstOption
    }
  }

  def findByZipcode(zipcode: Int): Option[Place] = DB.withSession {
    implicit session:Session => {
      Places.byCode(zipcode).firstOption
    }
  }

  def findByCity(city: String): Option[Place] = DB.withSession {
    implicit session:Session => {
      Places.byCity(city).firstOption
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

