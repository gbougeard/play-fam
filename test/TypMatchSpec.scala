import generators.TypMatchGen
import models.TypMatch
import models.TypMatchJson._

import play.api.libs.json.{JsSuccess, Json}

import org.specs2.ScalaCheck

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class TypMatchSpec extends FamSpecification with ScalaCheck with TypMatchGen {
  "json from(to) iso" ! prop {
    (p: TypMatch) =>
//          println(s"p: $p")
//          println(s"toJson: ${Json.toJson(p)}")
//          println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }


}



