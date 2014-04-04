package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._
import database.Places
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary._
import scala.Some

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


object Places extends DAO{

  lazy val pageSize = 10

  def findAll(implicit session: Session): Seq[Place] =  {
      (for (c <- places.sortBy(_.id)) yield c)
        .list
  }

  def count(implicit session: Session): Int =  {
      places.length.run
  }

  def placesWithCoords(implicit session: Session): Seq[Place] =  {
      (for {c <- places sortBy (_.zipcode)
            if (c.latitude isNotNull)
            if (c.longitude isNotNull)
      } yield c).list
  }

  def placesWithoutCoords(implicit session: Session): Seq[Place] =  {
      (for {c <- places sortBy (_.zipcode)
            if (c.latitude isNotNull)
            if (c.longitude isNotNull)
      } yield c)
        .take(20)
        .list
  }

  def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[Place] = {

    val offset = pageSize * page

        val places = (
          for {c <- places
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

  def findById(id: Long)(implicit session: Session): Option[Place] =  {
      places.where(_.id === id).firstOption
  }

  def findByName(name: String)(implicit session: Session): Seq[Place] =  {
      places.where(_.name === name).list
  }

  def findByZipcode(zipcode: String)(implicit session: Session): Seq[Place] =  {
      places.where(_.zipcode === zipcode).list
  }

  def findLikeZipcode(c: String)(implicit session: Session): Seq[Place] =  {
      places.where(_.zipcode like s"$c%").list
  }

  def findByCity(c: String)(implicit session: Session): Seq[Place] =  {
      places.where(_.city === c).list
  }

  def findLikeCity(c: String)(implicit session: Session): Seq[Place] =  {
      places.where(_.city like s"$c%").list
  }

  def findDups(place: Place)(implicit session: Session): Seq[Place] =  {
      play.Logger.debug(s"findDups for $place")
      val q = for {p <- places
                   if p.id =!= place.id
                   if p.name === place.name
                   if p.zipcode === place.zipcode
                   if p.city === place.city

      } yield p
      q.list
  }

  def insert(place: Place)(implicit session: Session): Long =  {
      places.insert(place)
  }

  def update(id: Long, place: Place)(implicit session: Session) =  {
      val place2update = place.copy(Some(id))
      places.where(_.id === id).update(place2update)
  }

  def delete(placeId: Long)(implicit session: Session) =  {
      play.Logger.info(s"delete Place $placeId")
      places.where(_.id === placeId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options(implicit session: Session): Seq[(String, String)] =  {
      val query = (for {
        item <- places
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  implicit val placeFormat = Json.format[Place]


}

trait PlaceGen {

  lazy val genPlace: Gen[Place] = for {
    id <- arbitrary[Long]
    name <- arbitrary[String]
    address <- arbitrary[String]
    city <- arbitrary[String]
    zipcode <- arbitrary[String]
    latitude <- arbitrary[Float]
    longitude <- arbitrary[Float]
    comments <- arbitrary[String]
    typFff <- arbitrary[String]
  } yield Place(
      Some(id),
      name,
      address,
      city,
      zipcode,
      Some(latitude),
      Some(longitude),
      Some(comments),
      Some(typFff))

  implicit lazy val arbPlace: Arbitrary[Place] = Arbitrary(genPlace)
}

