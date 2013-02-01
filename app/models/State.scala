package models

import common.{Profile, AppDB}

import play.api.libs.json._
import play.api.libs.functional.syntax._

import AppDB.dal.Countries._


case class State(id: Option[Long],
                 code: String,
                 name: String,
                 upper: String,
                 lower: String,
                 countryId: Long)


trait StateComponent {
  this: Profile =>

  import profile.simple._

  // define tables
  object States extends Table[State]("fam_state") {

    def id = column[Long]("id_state", O.PrimaryKey, O.AutoInc)

    def code = column[String]("cod_state")

    def name = column[String]("lib_state")

    def upper = column[String]("lib_Upper")

    def lower = column[String]("lib_lower")

    def countryId = column[Long]("id_country")

    def * = id.? ~ code ~ name ~ upper ~ lower ~ countryId <>(State, State.unapply _)

    def autoInc = id.? ~ code ~ name ~ upper ~ lower ~ countryId <>(State, State.unapply _)

    // A reified foreign key relation that can be navigated to create a join
    def country = foreignKey("COUNTRY_FK", countryId, AppDB.dal.Countries)(_.id)

    val byId = createFinderBy(_.id)
    val byName = createFinderBy(_.name)
    val byCode = createFinderBy(_.code)
    val byUpper = createFinderBy(_.upper)
    val byLower = createFinderBy(_.lower)
    val byCountry = createFinderBy(_.countryId)

    lazy val pageSize = 10

    def findAll(implicit session: Session): Seq[State] = {
      (for (c <- States.sortBy(_.name)) yield c).list
    }

    def count(implicit session: Session): Int = {
      (for {c <- States} yield c.id).list.size
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(State, Country)] = {

      val offset = pageSize * page

      val states = (
        for {s <- States
          .sortBy(_.lower)
          .drop(offset)
          .take(pageSize)
             c <- s.country
        } yield (s, c)).list

      val totalRows = count
      Page(states, page, offset, totalRows)
    }

    def findById(id: Long)(implicit session: Session): Option[State] = {
      States.byId(id).firstOption
    }

    def findByName(name: String)(implicit session: Session): Option[State] = {
      States.byName(name).firstOption
    }

    def findByCode(code: String)(implicit session: Session): Option[State] = {
      States.byCode(code).firstOption
    }

    def insert(state: State)(implicit session: Session): Long = {
      States.autoInc.insert((state))
    }

    def update(state: State)(implicit session: Session) = {
      States.where(_.id === state.id).update(state)
    }

    def delete(stateId: Long)(implicit session: Session) = {
      States.where(_.id === stateId).delete
    }

    def json(page: Int, pageSize: Int, orderField: Int)(implicit session: Session): Seq[(State, Country)] = {

      val states = for {s <- States
        .sortBy(state => orderField match {
        case 1 => state.id.asc
        case -1 => state.id.desc
        case 2 => state.code.asc
        case -2 => state.code.desc
        case 3 => state.name.asc
        case -3 => state.name.desc
      })
        .drop(page)
        .take(pageSize)
                        c <- s.country
      } yield (s, c)
      //      Json.toJson(cities.list)
      states.list

    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options(implicit session: Session): Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.name)

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

}