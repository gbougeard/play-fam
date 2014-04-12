package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Logger

case class FormationItem(id: Option[Long],
                         coord: Int,
                         numItem: Int,
                         formationId: Long)


object FormationItems extends DAO{

  def findByFormation(id: Long): Seq[FormationItem] =  DB.withSession {
    implicit session =>
      val query = for {fi <- formationItems
                       if fi.formationId === id
      } yield fi
      query.list
  }

  def insert(formationItem: FormationItem): Long =  DB.withSession {
    implicit session =>
      formationItems.insert(formationItem)
  }

  def update(id: Long, formationItem: FormationItem) =  DB.withSession {
    implicit session =>
      val formationItem2update = formationItem.copy(Some(id))
      formationItems.where(_.id === id).update(formationItem2update)
  }

  def delete(formationItemId: Long) =  DB.withSession {
    implicit session =>
      formationItems.where(_.id === formationItemId).delete
  }

  def save(items: Seq[FormationItem]) = DB.withSession {
    implicit session =>
    items.map(item => update(item.id.getOrElse(0), item))
  }

  def init(formationId: Long) =  DB.withSession {
    implicit session =>
      Formations.findById(formationId).map {
        formation => TypMatches.findById(formation.typMatchId).map {
          typMatch => for (i <- 1 to typMatch.nbPlayer) {
            insert(new FormationItem(None, i, i, formationId))
          }
        }
      }
  }

  def copy(formationIdToCopy: Long, formationId: Long) =  DB.withSession {
    implicit session =>
      findByFormation(formationIdToCopy).map {
        item => insert(new FormationItem(None, item.coord, item.numItem, formationId))
    }
  }

}

