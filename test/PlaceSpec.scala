import models.{PlaceGen, Place}
import models.Place._
import org.specs2.mutable._

import org.specs2.ScalaCheck

import play.api.libs.json.{JsSuccess, Json}

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class PlaceSpec extends Specification with ScalaCheck with PlaceGen {

  "json from(to) iso" ! prop {
    (p: Place) => Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }
}