package models.common

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models.{Event, Place, Player}

/**
 * Created with IntelliJ IDEA.
 * User: gbougeard
 * Date: 03/01/13
 * Time: 15:40
 * To change this template use File | Settings | File Templates.
 */
object JsonImplicits {
  implicit val playerFormat = Json.format[Player]
  implicit val placeFormat = Json.format[Place]
  implicit val eventFormat = Json.format[Event]

}
