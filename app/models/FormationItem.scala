package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB
import play.api.Logger
import play.api.libs.json._
import database.FormationItems

case class FormationItem(id: Option[Long],
                         coord: Int,
                         numItem: Int,
                         formationId: Long)


object FormationItem {

  def findByFormation(id: Long): Seq[FormationItem] =  {
    implicit session: Session => {
      val query = for {fi <- FormationItems
                       if fi.formationId === id
      } yield fi
      query.list
    }
  }

  def insert(formationItem: FormationItem): Long =  {
    implicit session: Session => {
      FormationItems.autoInc.insert(formationItem)
    }
  }

  def update(id: Long, formationItem: FormationItem) =  {
    implicit session: Session => {
      val formationItem2update = formationItem.copy(Some(id))
      FormationItems.where(_.id === id).update(formationItem2update)
    }
  }

  def delete(formationItemId: Long) =  {
    implicit session: Session => {
      FormationItems.where(_.id === formationItemId).delete
    }
  }

  def save(items: Seq[FormationItem]) {
    items.map(item => update(item.id.getOrElse(0), item))
  }

  def init(formationId: Long) =  {
    implicit session: Session => {
      Formation.findById(formationId).map {
        formation => TypMatch.findById(formation.typMatchId).map {
          typMatch => for (i <- 1 to typMatch.nbPlayer) {
            insert(new FormationItem(None, i, i, formationId))
          }
        }
      }
    }
  }

  def copy(formationIdToCopy: Long, formationId: Long) =  {
    implicit session: Session => {
      findByFormation(formationIdToCopy).map {
        item => insert(new FormationItem(None, item.coord, item.numItem, formationId))
      }
    }
  }

  implicit val formationItemFormat = Json.format[FormationItem]

}

