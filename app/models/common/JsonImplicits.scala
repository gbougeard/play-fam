package models.common

import play.api.libs.json._
import play.api.libs.functional.syntax._
import models._
import models.Player
import models.Event
import models.Province
import models.Place

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
  implicit val cityFormat = Json.format[City]
  implicit val provinceFormat = Json.format[Province]
  implicit val cityWithProvinceReads: Reads[(City, Province)] = (
    (__ \ 'city).read[City] ~
      (__ \ 'province).read[Province]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val cityWithProvinceWrites: Writes[(City, Province)] = (
    (__ \ 'city).write[City] ~
      (__ \ 'province).write[Province]
    ) tupled
  implicit val cityWithProvinceFormat = Format(cityWithProvinceReads, cityWithProvinceWrites)

}
