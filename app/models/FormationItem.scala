package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._

case class FormationItem(id: Option[Long],
                         coord: Int,
                         numItem: Int,
                         formationId: Long)


// define tables
object FormationItems extends Table[FormationItem]("fam_formation_item") {

  def id = column[Long]("id_formation_item", O.PrimaryKey, O.AutoInc)

  def coord = column[Int]("coord")

  def numItem = column[Int]("num_item")

  def formationId = column[Long]("id_formation")

  def * = id.? ~ coord ~ numItem ~ formationId <>(FormationItem, FormationItem.unapply _)

  def autoInc = id.? ~  coord ~ numItem ~ formationId <>(FormationItem, FormationItem.unapply _) returning id

  // A reified foreign key relation that can be navigated to create a join
  def formation = foreignKey("FORMATION_FK", formationId, Formations)(_.id)

  def findByFormation(id: Long): Seq[FormationItem] = DB.withSession {
    implicit session => {
      val query = (
        for {fi <- FormationItems
             if fi.formationId === id
        } yield fi)
      query.list
    }
  }

  def insert(formationItem: FormationItem): Long = DB.withSession {
    implicit session => {
      FormationItems.autoInc.insert((formationItem))
    }
  }

  def update(id: Long, formationItem: FormationItem) = DB.withSession {
    implicit session => {
      val formationItem2update = formationItem.copy(Some(id))
      FormationItems.where(_.id === id).update(formationItem2update)
    }
  }

  def delete(formationItemId: Long) = DB.withSession {
    implicit session => {
      FormationItems.where(_.id === formationItemId).delete
    }
  }

  implicit val formationItemFormat = Json.format[FormationItem]

}

