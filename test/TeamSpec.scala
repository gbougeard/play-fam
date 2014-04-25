import generators.{ClubGen,TeamGen}
import models.Team
import models.TeamJson._

import play.api.libs.json.{JsSuccess, Json}

import org.specs2.ScalaCheck

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */

class TeamSpec extends FamSpecification with ScalaCheck with TeamGen with ClubGen {
  "json from(to) iso" ! prop {
    (p: Team) =>
//          println(s"p: $p")
//          println(s"toJson: ${Json.toJson(p)}")
//          println(s"from(to): ${Json.fromJson(Json.toJson(p))}")
      Json.fromJson(Json.toJson(p)) == JsSuccess(p)
  }


}



