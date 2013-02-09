package models

import play.api.db.DB

import play.api.Play.current

import scala.slick.driver.MySQLDriver.simple._
import scala.slick.session.Database

import play.api.libs.json._
import play.api.libs.functional.syntax._


// Use the implicit threadLocalSession

import scala.slick.session.Database.threadLocalSession

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

  lazy val database = Database.forDataSource(DB.getDataSource())
  lazy val pageSize = 10

  def findAll: Seq[Place] = database withSession {
    (for (c <- Places.sortBy(_.name)) yield c).list
  }

  def placesWithCoords: Seq[Place] = database withSession {
    (for {c <- Places sortBy (_.name)
          if (c.latitude isNotNull)
          if (c.longitude isNotNull)
    } yield c).list
  }

  def findPage(page: Int = 0, orderField: Int): Page[Place] = {

    val offset = pageSize * page

    database withSession {
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

      val totalRows = (for (c <- Places) yield c.id).list.size
      Page(places, page, offset, totalRows)
    }
  }

  def findById(id: Long): Option[Place] = database withSession {
    Places.byId(id).firstOption
  }

  def findByName(name: String): Option[Place] = database withSession {
    Places.byName(name).firstOption
  }

  def findByZipcode(zipcode: Int): Option[Place] = database withSession {
    Places.byCode(zipcode).firstOption
  }

  def findByCity(city: String): Option[Place] = database withSession {
    Places.byCity(city).firstOption
  }

  def insert(place: Place): Long = database withSession {
    Places.autoInc.insert((place))
  }

  def update(id: Long, place: Place) = database withSession {
    val place2update = place.copy(Some(id))
    Places.where(_.id === id).update(place2update)
  }

  def delete(placeId: Long) = database withSession {
    Places.where(_.id === placeId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  implicit val placeFormat = Json.format[Place]


}

