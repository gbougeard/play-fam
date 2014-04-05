package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import play.api.Logger
import scala.util.Try

import database.PlaceClubs

case class PlaceClub(placeId: Long,
                     clubId: Long)

object PlaceClubs extends DAO{

  lazy val pageSize = 10

  //  def findPage(page: Int = 0, orderField: Int): Page[(Place)] = {
  //
  //    val offset = pageSize * page
  //
  //     {
  //      implicit session:Session => {
  //        val places = (
  //          for {t <- Places
  //            .sortBy(place => orderField match {
  //            case 1 => place.firstName.asc
  //            case -1 => place.firstName.desc
  //            case 2 => place.lastName.asc
  //            case -2 => place.lastName.desc
  //            case 3 => place.email.asc
  //            case -3 => place.email.desc
  //          })
  //            .drop(offset)
  //            .take(pageSize)
  //          } yield (t)).list
  //
  //        val totalRows = (for {t <- Places} yield t.id).list.size
  //        Page(places, page, offset, totalRows)
  //      }
  //    }
  //  }

  def findByClub(id: Long): Seq[(PlaceClub, Place, Club)] =  DB.withSession {
    implicit session =>
      val query = for {ps <- placeClubs
                       if ps.clubId === id
                       p <- ps.place
                       s <- ps.club

      } yield (ps, p, s)
      query.list
  }

  def findByPlace(id: Long): Seq[(PlaceClub, Place, Club)] =  DB.withSession {
    implicit session =>
      val query = for {ps <- placeClubs
                       if ps.placeId === id
                       p <- ps.place
                       s <- ps.club

      } yield (ps, p, s)
      query.list
  }

  def insert(placeClub: PlaceClub): Long =  DB.withSession {
    implicit session =>
      play.Logger.debug(s"insert $placeClub")
      placeClubs.insert(placeClub)
  }

  def insert(pcLst: Seq[PlaceClub]):Try[Option[Int]] =  DB.withSession {
    implicit session =>
      Try(placeClubs.insertAll(pcLst:_*))
  }

  //
  //    def update(idPlace: Long,idClub: Long, place: PlaceClub) =  {
  //      implicit session:Session => {
  //        val place2update = place.copy(Some(id))
  //        Logger.info("playe2update " + place2update)
  //        placeClubs.where(_.id === id).update(place2update)
  //      }
  //    }

  def delete(idPlace: Long, idClub: Long) =  DB.withSession {
    implicit session =>
      val q = for {ps <- placeClubs
                   if ps.placeId === idPlace
                   if ps.clubId === idClub
      } yield ps
      q.delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  //  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.firstName + " " + c.lastName)

  //  def options: Seq[(String, String)] =  {
  //    implicit session:Session =>
  //      val query = (for {
  //        item <- Places
  //      } yield (item.id, item.firstName + " " + item.lastName)
  //        ).sortBy(_._2)
  //      query.list.map(row => (row._1.toString, row._2))
  //  }

  implicit val pcFormat = Json.format[PlaceClub]

  implicit val pcCompleteReads: Reads[(PlaceClub, Place, Club)] = (
    (__ \ 'placeclub).read[PlaceClub] ~
      (__ \ 'place).read[Place] ~
      (__ \ 'club).read[Club]
    ) tupled

  implicit val mtCompleteWrites: Writes[(PlaceClub, Place, Club)] = (
    (__ \ 'placeclub).write[PlaceClub] ~
      (__ \ 'place).write[Place] ~
      (__ \ 'club).write[Club]
    ) tupled
}