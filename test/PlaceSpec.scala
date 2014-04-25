import generators.PlaceGen
import models.Place
import models.PlaceJson._

import org.scalacheck.Arbitrary._
import org.scalacheck.{Arbitrary, Gen}
import org.specs2.ScalaCheck

import play.api.libs.json.{JsSuccess, Json}
import play.api.test.PlaySpecification
import scala.Some

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class PlaceSpec extends PlaySpecification with ScalaCheck with PlaceGen {

  "json from(to) iso" ! prop {
    (p: Place) =>
    //      println(s"p: $p")
    //      println(s"toJson: ${Json.toJson(p)}")
    //      println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }
}




