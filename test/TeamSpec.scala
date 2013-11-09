import models.{TeamGen, Team}
import models.Team._
import org.specs2.mutable._

import org.specs2.ScalaCheck

import play.api.libs.json.{JsSuccess, Json}

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class TeamSpec extends Specification with ScalaCheck with TeamGen {
  "json from(to) iso" ! prop {
    (p: Team) =>
//          println(s"p: $p")
//          println(s"toJson: ${Json.toJson(p)}")
//          println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }
}