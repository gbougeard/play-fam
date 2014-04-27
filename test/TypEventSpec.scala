import generators.TypEventGen

import models.TypEvent
import models.TypEventJson._

import play.api.libs.json.{JsSuccess, Json}

import org.specs2.ScalaCheck

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class TypEventSpec extends FamSpecification with ScalaCheck with TypEventGen {
  "json from(to) iso" ! prop {
    (p: TypEvent) =>
//          println(s"p: $p")
//          println(s"toJson: ${Json.toJson(p)}")
//          println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }


}



