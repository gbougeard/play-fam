package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB

import play.api.libs.json._
import play.api.libs.functional.syntax._

import database.States

case class State(id: Option[Long],
                 code: String,
                 name: String,
                 upper: String,
                 lower: String,
                 countryId: Long)


object State{
  lazy val pageSize = 10

  def findAll: Seq[State] =  {
    implicit session:Session => {
      (for (c <- States.sortBy(_.name)) yield c).list
    }
  }

  def count: Int =  {
    implicit session:Session => {
      Query(States.length).first
    }
  }

  def findPage(page: Int = 0, orderField: Int): Page[(State, Country)] = {

    val offset = pageSize * page

     {
      implicit session:Session => {

        val states = (
          for {s <- States
               c <- s.country
          } yield (s, c))
          .sortBy(orderField match {
          case 1 => _._1.code asc
          case -1 => _._1.code desc
          case 2 => _._1.name asc
          case -2 => _._1.name desc
          case 3 => _._2.name asc
          case -3 => _._2.name desc
        })
          .drop(offset)
          .take(pageSize)

        val totalRows = count
        Page(states.list, page, offset, totalRows)
      }
    }
  }

  def findById(id: Long): Option[State] =  {
    implicit session:Session => {
      States.byId(id).firstOption
    }
  }

  def findByName(name: String): Option[State] =  {
    implicit session:Session => {
      States.byName(name).firstOption
    }
  }

  def findByCode(code: String): Option[State] =  {
    implicit session:Session => {
      States.byCode(code).firstOption
    }
  }

  def insert(state: State): Long =  {
    implicit session:Session => {
      States.autoInc.insert(state)
    }
  }

  def update(id: Long, state: State) =  {
    implicit session:Session => {
      States.where(_.id === state.id).update(state.copy(Some(id)))
    }
  }

  def delete(stateId: Long) =  {
    implicit session:Session => {
      States.where(_.id === stateId).delete
    }
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  {
    implicit session:Session =>
      val query = (for {
        item <- States
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

  //JSON
  implicit val stateFormat = Json.format[State]

  implicit val stateWithCountryReads: Reads[(State, Country)] = (
    (__ \ 'state).read[State] ~
      (__ \ 'country).read[Country]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val stateWithCountryWrites: Writes[(State, Country)] = (
    (__ \ 'state).write[State] ~
      (__ \ 'country).write[Country]
    ) tupled
  implicit val stateWithCountryFormat = Format(stateWithCountryReads, stateWithCountryWrites)

}