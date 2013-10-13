package models.database

import play.api.db.slick.Config.driver.simple._
import models.TypCard

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:41
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object TypCards extends Table[TypCard]("fam_typ_card") {

  def id = column[Long]("id_typ_card", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_card")

  def code = column[String]("cod_typ_card")

  def * = id.? ~ code ~ name <>(TypCard.apply _, TypCard.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)


}
