package models

import play.api.Play.current

import play.api.db.slick.Config.driver.simple._
import play.api.db.slick.DB


case class State(id: Option[Long],
                 code: String,
                 name: String,
                 upper: String,
                 lower: String,
                 countryId: Long)


object States extends DAO{
  lazy val pageSize = 10

  def findAll: Seq[State] =  DB.withSession {
    implicit session =>
      (for (c <- states.sortBy(_.name)) yield c).list
  }

  def count: Int =  DB.withSession {
    implicit session =>
      states.length.run
  }

  def findPage(page: Int = 0, orderField: Int): Page[(State, Country)] = DB.withSession {
    implicit session =>

    val offset = pageSize * page


        val q = (
          for {s <- states
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
        Page(q.list, page, offset, totalRows)
  }

  def findById(id: Long): Option[State] =  DB.withSession {
    implicit session =>
      states.where(_.id === id).firstOption
  }

  def findByName(name: String): Option[State] =  DB.withSession {
    implicit session =>
      states.where(_.name === name).firstOption
  }

  def findByCode(code: String): Option[State] =  DB.withSession {
    implicit session =>
      states.where(_.code === code).firstOption
  }

  def insert(state: State): Long =  DB.withSession {
    implicit session =>
      states.insert(state)
  }

  def update(id: Long, state: State) =  DB.withSession {
    implicit session =>
      states.where(_.id === state.id).update(state.copy(Some(id)))
  }

  def delete(stateId: Long) =  DB.withSession {
    implicit session =>
      states.where(_.id === stateId).delete
  }

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
//  def options: Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)
  def options: Seq[(String, String)] =  DB.withSession {
    implicit session =>
      val query = (for {
        item <- states
      } yield (item.id, item.name)
        ).sortBy(_._2)
      query.list.map(row => (row._1.toString, row._2))
  }

}