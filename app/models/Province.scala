package models

import common.{AppDB, Profile}
import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.common.AppDB.dal.States._


case class Province(id: Option[Long],
                    code: String,
                    name: String,
                    upper: String,
                    lower: String,
                    stateId: Long)

trait ProvinceComponent {
  this: Profile =>

  import profile.simple._

  // define tables
  object Provinces extends Table[Province]("fam_province") {

    def id = column[Long]("id_province", O.PrimaryKey, O.AutoInc)

    def code = column[String]("cod_province")

    def name = column[String]("lib_province")

    def upper = column[String]("lib_Upper")

    def lower = column[String]("lib_lower")

    def stateId = column[Long]("id_state")

    def * = id.? ~ code ~ name ~ upper ~ lower ~ stateId <>(Province, Province.unapply _)
    def autoInc = id.? ~ code ~ name ~ upper ~ lower ~ stateId <>(Province, Province.unapply _) returning id

    // A reified foreign key relation that can be navigated to create a join
    def state = foreignKey("STATE_FK", stateId, AppDB.dal.States)(_.id)

    val byId = createFinderBy(_.id)
    val byName = createFinderBy(_.name)
    val byCode = createFinderBy(_.code)
    val byUpper = createFinderBy(_.upper)
    val byLower = createFinderBy(_.lower)
    val byState = createFinderBy(_.stateId)

    lazy val pageSize = 10

    def findAll(implicit session: Session): Seq[Province] = {
      (for (c <- Provinces.sortBy(_.code)) yield c).list
    }

    def count(implicit session: Session): Int = {
      (for {c <- Provinces} yield c.id).list.size
    }

    def findPage(page: Int = 0, orderField: Int)(implicit session: Session): Page[(Province, State)] = {

      val offset = pageSize * page

      val provinces = (
        for {p <- Provinces
          .sortBy(_.code)
          .drop(offset)
          .take(pageSize)
             s <- p.state
        } yield (p, s)).list

      val totalRows = count
      Page(provinces, page, offset, totalRows)
    }

    def findById(id: Long)(implicit session: Session): Option[Province] = {
      Provinces.byId(id).firstOption
    }

    def findByName(name: String)(implicit session: Session): Option[Province] = {
      Provinces.byName(name).firstOption
    }

    def findByCode(code: String)(implicit session: Session): Option[Province] = {
      Provinces.byCode(code).firstOption
    }

    def insert(province: Province)(implicit session: Session): Long = {
      Provinces.autoInc.insert((province))
    }

    def update(province: Province)(implicit session: Session) = {
      Provinces.where(_.id === province.id).update(province)
    }

    def delete(provinceId: Long)(implicit session: Session) = {
      Provinces.where(_.id === provinceId).delete
    }

    def json(page: Int, pageSize: Int, orderField: Int)(implicit session: Session): Seq[(Province, State)] = {

      val provinces = for {p <- Provinces
        .sortBy(province => orderField match {
        case 1 => province.id.asc
        case -1 => province.id.desc
        case 2 => province.code.asc
        case -2 => province.code.desc
        case 3 => province.name.asc
        case -3 => province.name.desc
      })
        .drop(page)
        .take(pageSize)
                           s <- p.state
      } yield (p, s)
      //      Json.toJson(cities.list)
      provinces.list

    }

    /**
     * Construct the Map[String,String] needed to fill a select options set.
     */
    def options(implicit session: Session): Seq[(String, String)] = for {c <- findAll} yield (c.id.toString, c.code + " - " + c.name)

    //JSON
    implicit val provinceFormat = Json.format[Province]

    implicit val provinceWithStateReads: Reads[(Province, State)] = (
      (__ \ 'province).read[Province] ~
        (__ \ 'state).read[State]
      ) tupled

    // or using the operators inspired by Scala parser combinators for those who know them
    implicit val provinceWithStateWrites: Writes[(Province, State)] = (
      (__ \ 'province).write[Province] ~
        (__ \ 'state).write[State]
      ) tupled
    implicit val provinceWithStateFormat = Format(provinceWithStateReads, provinceWithStateWrites)
  }

}