package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._
import database.Places

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


object Place{

  lazy val pageSize = 10

  def findAll: Seq[Place] = DB.withSession {
    implicit session: Session => {
      (for (c <- Places.sortBy(_.id)) yield c)
        .list
    }
  }

  def count: Int = DB.withSession {
    implicit session: Session => {
      Query(Places.length).first
    }
  }

  def placesWithCoords: Seq[Place] = DB.withSession {
    implicit session: Session => {
      (for {c <- Places sortBy (_.zipcode)
            if (c.latitude isNotNull)
            if (c.longitude isNotNull)
      } yield c).list
    }
  }

  def placesWithoutCoords: Seq[Place] = DB.withSession {
    implicit session: Session => {
      (for {c <- Places sortBy (_.zipcode)
            if (c.latitude isNotNull)
            if (c.longitude isNotNull)
      } yield c)
        .take(20)
        .list
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[Place] = {

    val offset = pageSize * page

    DB.withSession {
      implicit session: Session => {
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
    implicit session: Session => {
      Places.byId(id).firstOption
    }
  }

  def findByName(name: String): Seq[Place] = DB.withSession {
    implicit session: Session => {
      Places.byName(name).list
    }
  }

  def findByZipcode(zipcode: String): Seq[Place] = DB.withSession {
    implicit session: Session => {
      Places.byCode(zipcode).list
    }
  }

  def findLikeZipcode(c: String): Seq[Place] = DB.withSession {
    implicit session: Session => {
      Query(Places).where(_.zipcode like s"$c%").list
    }
  }

  def findByCity(c: String): Seq[Place] = DB.withSession {
    implicit session: Session => {
      Places.byCity(c).list
    }
  }

  def findLikeCity(c: String): Seq[Place] = DB.withSession {
    implicit session: Session => {
      Query(Places).where(_.city like s"$c%").list
    }
  }

  def findDups(place: Place):Seq[Place] = DB.withSession {
    implicit session: Session => {
      play.Logger.debug(s"findDups for $place")
      val q = for{ p <- Places
        if p.id =!= place.id
        if p.name === place.name
        if p.zipcode === place.zipcode
        if p.city === place.city

      } yield p
      q.list
    }
  }

  def insert(place: Place): Long = DB.withSession {
    implicit session: Session => {
      Places.autoInc.insert(place)
    }
  }

  def update(id: Long, place: Place) = DB.withSession {
    implicit session: Session => {
      val place2update = place.copy(Some(id))
      Places.where(_.id === id).update(place2update)
    }
  }

  def delete(placeId: Long) = DB.withSession {
    implicit session: Session => {
      play.Logger.info(s"delete Place $placeId")
      Places.where(_.id === placeId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] = DB.withSession {
    implicit session: Session =>
      val query = (for {
        item <- Places
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val placeFormat = Json.format[Place]


}

