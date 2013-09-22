package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

case class PlaceClub(placeId: Long,
                     clubId: Long)

// define tables
object PlaceClubs extends Table[PlaceClub]("fam_club_place") {

  def placeId = column[Long]("id_place")

  def clubId = column[Long]("id_club")

  def * = placeId ~ clubId <>(PlaceClub, PlaceClub.unapply _)

  // A reified foreign key relation that can be navigated to create a join
  def place = foreignKey("PLACE_FK", placeId, Places)(_.id)

  def club = foreignKey("CLUB_FK", clubId, Clubs)(_.id)


  def findByClub(id: Long): Seq[(PlaceClub, Place, Club)] = DB.withSession {
    implicit session: Session => {
      val query = for {ps <- PlaceClubs
                       if ps.clubId === id
                       p <- ps.place
                       s <- ps.club

      } yield (ps, p, s)
      query.list
    }
  }

  def findByPlace(id: Long): Seq[(PlaceClub, Place, Club)] = DB.withSession {
    implicit session: Session => {
      val query = for {ps <- PlaceClubs
                       if ps.placeId === id
                       p <- ps.place
                       s <- ps.club

      } yield (ps, p, s)
      query.list
    }
  }

  def insert(placeClub: PlaceClub): Long = DB.withSession {
    implicit session: Session => {
      PlaceClubs.insert(placeClub)
    }
  }

  //
  //    def update(idPlace: Long,idClub: Long, place: PlaceClub) = DB.withSession {
  //      implicit session:Session => {
  //        val place2update = place.copy(Some(id))
  //        Logger.info("playe2update " + place2update)
  //        PlaceClubs.where(_.id === id).update(place2update)
  //      }
  //    }

  def delete(idPlace: Long, idClub: Long) = DB.withSession {
    implicit session: Session => {
      val q = for {ps <- PlaceClubs
                   if ps.placeId === idPlace
                   if ps.clubId === idClub
      } yield ps
      q.delete
    }
  }

}