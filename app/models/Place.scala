package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


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

object PlaceJson {
  import play.api.libs.json.Json
  implicit val placeJsonFormat = Json.format[Place]
}

object Places extends DAO{

  lazy val pageSize = 10

  def findAll: Seq[Place] =  DB.withSession {
    implicit session =>
      (for (c <- places.sortBy(_.id)) yield c)
        .list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      places.length.run
  }

  def placesWithCoords: Seq[Place] =  DB.withSession {
    implicit session =>
      (for {c <- places sortBy (_.zipcode)
            if (c.latitude isNotNull)
            if (c.longitude isNotNull)
      } yield c).list
  }

  def placesWithoutCoords: Seq[Place] =  DB.withSession {
    implicit session =>
      (for {c <- places sortBy (_.zipcode)
            if (c.latitude isNotNull)
            if (c.longitude isNotNull)
      } yield c)
        .take(20)
        .list
  }

  def findPage(page: Int = 0, orderField: Int): Page[Place] = DB.withSession {
    implicit session =>

    val offset = pageSize * page

        val q = for {c <- places
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
        } yield c

        Page(q.list, page, offset, count)
  }

  def findById(id: Long): Option[Place] =  DB.withSession {
    implicit session =>
      places.where(_.id === id).firstOption
  }

  def findByName(name: String): Seq[Place] =  DB.withSession {
    implicit session =>
      places.where(_.name === name).list
  }

  def findByZipcode(zipcode: String): Seq[Place] =  DB.withSession {
    implicit session =>
      places.where(_.zipcode === zipcode).list
  }

  def findLikeZipcode(c: String): Seq[Place] =  DB.withSession {
    implicit session =>
      places.where(_.zipcode like s"$c%").list
  }

  def findByCity(c: String): Seq[Place] =  DB.withSession {
    implicit session =>
      places.where(_.city === c).list
  }

  def findLikeCity(c: String): Seq[Place] =  DB.withSession {
    implicit session =>
      places.where(_.city like s"$c%").list
  }

  def findDups(place: Place): Seq[Place] =  DB.withSession {
    implicit session =>
      play.Logger.debug(s"findDups for $place")
      val q = for {p <- places
                   if p.id =!= place.id
                   if p.name === place.name
                   if p.zipcode === place.zipcode
                   if p.city === place.city

      } yield p
      q.list
  }

  def insert(place: Place): Long =  DB.withSession {
    implicit session =>
      places.insert(place)
  }

  def update(id: Long, place: Place) =  DB.withSession {
    implicit session =>
      val place2update = place.copy(Some(id))
      places.where(_.id === id).update(place2update)
  }

  def delete(placeId: Long) =  DB.withSession {
    implicit session =>
      play.Logger.info(s"delete Place $placeId")
      places.where(_.id === placeId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- places
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}
