package models

import common.Profile

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

trait PlaceComponent {
  this: Profile =>

  import profile.simple._

  implicit val placeFormat = Json.format[Place]

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

    def findAll(implicit session: Session): Seq[Place] = {
      (for (c <- Places.sortBy(_.name)) yield c).list
    }

    def placesWithCoords(implicit session: Session): Seq[Place] = {
      (for {c <- Places sortBy(_.name)
            if (c.latitude isNotNull)
            if (c.longitude isNotNull)
      } yield c).list
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Place] = {

      val offset = pageSize * page
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

    def findById(id: Long)(implicit session: Session): Option[Place] = {
      Places.byId(id).firstOption
    }

    def findByName(name: String)(implicit session: Session): Option[Place] = {
      Places.byName(name).firstOption
    }

    def findByZipcode(zipcode: Int)(implicit session: Session): Option[Place] = {
      Places.byCode(zipcode).firstOption
    }

    def findByCity(city: String)(implicit session: Session): Option[Place] = {
      Places.byCity(city).firstOption
    }

    def insert(place: Place)(implicit session: Session): Long = {
      Places.autoInc.insert((place))
    }

    def update(place: Place)(implicit session: Session) = {
      Places.where(_.id === place.id).update(place)
    }

    def delete(placeId: Long)(implicit session: Session) = {
      Places.where(_.id === placeId).delete
    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options(implicit session: Session): Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

  }

}

