package models.database

import play.api.db.slick.Config.driver.simple._
import models.Answer

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 12/10/13
 * Time: 16:52
 * To change this template use File | Settings | File Templates.
 */
// define tables
private[models] object Answers extends Table[Answer]("fam_answer") {

  def id = column[Long]("id_answer")

  def eventId = column[Long]("id_event")

  def playerId = column[Long]("id_player")

  def typAnswerId = column[Long]("id_typ_answer")

  def comments = column[String]("comments")

  def * = id.? ~ eventId ~ playerId ~ typAnswerId ~ comments.? <>(Answer.apply _, Answer.unapply _)

  def autoInc = * returning id

  // A reified foreign key relation that can be navigated to create a join
  def event = foreignKey("EVENT_FK", eventId, Events)(_.id)

  def player = foreignKey("PLAYER_FK", playerId, Players)(_.id)

  def typAnswer = foreignKey("TYP_ANSWER_FK", typAnswerId, TypAnswers)(_.id)


}
