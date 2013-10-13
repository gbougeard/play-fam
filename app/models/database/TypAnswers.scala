package models.database

import play.api.db.slick.Config.driver.simple._
import models.TypAnswer

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 19:40
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object TypAnswers extends Table[TypAnswer]("fam_typ_answer") {

  def id = column[Long]("id_typ_answer", O.PrimaryKey, O.AutoInc)

  def name = column[String]("lib_typ_answer")

  def code = column[String]("cod_typ_answer")

  def group = column[String]("grp_typ_answer")

  def * = id.? ~ code ~ name ~ group <>(TypAnswer.apply _, TypAnswer.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  //  def typMatch = foreignKey("TYP_MATCH_FK", typMatchId, TypMatches)(_.id)

  val byId = createFinderBy(_.id)
  val byName = createFinderBy(_.name)
  val byCode = createFinderBy(_.code)
  val byGroup = createFinderBy(_.group)


}
