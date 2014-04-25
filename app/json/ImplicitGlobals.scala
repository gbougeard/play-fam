package json
import play.api.libs.json._
import play.api.libs.functional.syntax._

import org.joda.time.DateTime
import models._
import models.AnswerJson._
import models.ClubJson._
import models.EventJson._
import models.PlaceJson._
import models.PlayerJson._
import models.TeamJson._
import models.TypAnswerJson._



/**
 * Created by gbougeard on 12/04/14.
 */
object ImplicitGlobals {
  implicit val cityFormat = Json.format[City]
  implicit val competitionTeamFormat = Json.format[CompetitionTeam]
  implicit val countryFormat = Json.format[Country]
//  implicit val famUserFormat = Json.format[FamUser]
  implicit val fixtureFormat = Json.format[Fixture]
  implicit val formationFormat = Json.format[Formation]
  implicit val formationItemFormat = Json.format[FormationItem]
  implicit val groupFormat = Json.format[Group]
  implicit val matchFormat = Json.format[Match]
  implicit val mtFormat = Json.format[MatchTeam]
  implicit val playerPositionFormat = Json.format[PlayerPosition]
  implicit val playerSeasonFormat = Json.format[PlayerSeason]
  implicit val positionFormat = Json.format[Position]
  implicit val provinceFormat = Json.format[Province]
  implicit val rankingFormat = Json.format[Ranking]
  implicit val roleFormat = Json.format[Role]
  implicit val scaleFormat = Json.format[Scale]
  implicit val seasonFormat = Json.format[Season]
  implicit val seasonCompetitionFormat = Json.format[SeasonCompetition]
  implicit val stateFormat = Json.format[State]
  implicit val typCardFormat = Json.format[TypCard]
  implicit val typCompetitionFormat = Json.format[TypCompetition]
  implicit val typMatchFormat = Json.format[TypMatch]

  implicit val cityWithProvinceReads: Reads[(City, Province)] = (
    (__ \ 'city).read[City] ~
      (__ \ 'province).read[Province]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val cityWithProvinceWrites: Writes[(City, Province)] = (
    (__ \ 'city).write[City] ~
      (__ \ 'province).write[Province]
    ) tupled

  val pattern = "yyyy-MM-dd'T'HH:mm:ss.sssZ"
  implicit val dateFormat = Format[DateTime](Reads.jodaDateReads(pattern), Writes.jodaDateWrites(pattern))

//  implicit val eventReads: Reads[Event] = (
//    (__ \ "id").readNullable[Long] ~
//      (__ \ "dtEvent").read[DateTime] ~
//      (__ \ "duration").read[Int] ~
//      (__ \ "name").read[String] ~
//      (__ \ "typEventId").read[Long] ~
//      (__ \ "placeId").read[Option[Long]] ~
//      (__ \ "eventStatusId").read[Long] ~
//      (__ \ "comments").read[Option[String]]
//    )(Event.apply _)
//
//  // or using the operators inspired by Scala parser combinators for those who know them
//  implicit val eventWrites: Writes[Event] = (
//    (__ \ "id").write[Option[Long]] ~
//      (__ \ "dtEvent").write[DateTime] ~
//      (__ \ "duration").write[Int] ~
//      (__ \ "name").write[String] ~
//      (__ \ "typEventId").write[Long] ~
//      (__ \ "placeId").write[Option[Long]] ~
//      (__ \ "eventStatusId").write[Long] ~
//      (__ \ "comments").write[Option[String]]
//    )(unlift(Event.unapply))


  implicit val mtCompleteReads: Reads[(MatchTeam, Team)] = (
    (__ \ 'matchteam).read[MatchTeam] ~
      (__ \ 'team).read[Team]
    ) tupled

  implicit val mtCompleteWrites: Writes[(MatchTeam, Team)] = (
    (__ \ 'matchteam).write[MatchTeam] ~
      (__ \ 'team).write[Team]
    ) tupled

  implicit val provinceWithStateReads: Reads[(Province, State)] = (
    (__ \ 'province).read[Province] ~
      (__ \ 'state).read[State]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val provinceWithStateWrites: Writes[(Province, State)] = (
    (__ \ 'province).write[Province] ~
      (__ \ 'state).write[State]
    ) tupled

  implicit val stateWithCountryReads: Reads[(State, Country)] = (
    (__ \ 'state).read[State] ~
      (__ \ 'country).read[Country]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val stateWithCountryWrites: Writes[(State, Country)] = (
    (__ \ 'state).write[State] ~
      (__ \ 'country).write[Country]
    ) tupled



}
