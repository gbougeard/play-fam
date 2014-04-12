package json

import org.joda.time.DateTime
import models._
import play.api.libs.json._
import play.api.libs.functional.syntax._


/**
 * Created by gbougeard on 12/04/14.
 */
object ImplicitGlobals {
  implicit val answerFormat = Json.format[Answer]
  implicit val cardFormat = Json.format[Card]
  implicit val categoryFormat = Json.format[Category]
  implicit val cityyFormat = Json.format[City]
  implicit val clubFormat = Json.format[Club]
  implicit val competitionTeamFormat = Json.format[CompetitionTeam]
  implicit val countryFormat = Json.format[Country]
  implicit val eventFormat = Json.format[Event]
  implicit val eventCategoryFormat = Json.format[EventCategory]
  implicit val eventStatusFormat = Json.format[EventStatus]
  implicit val eventTeamFormat = Json.format[EventTeam]
//  implicit val famUserFormat = Json.format[FamUser]
  implicit val fixtureFormat = Json.format[Fixture]
  implicit val formationFormat = Json.format[Formation]
  implicit val formationItemFormat = Json.format[FormationItem]
  implicit val goalFormat = Json.format[Goal]
  implicit val groupFormat = Json.format[Group]
  implicit val matchFormat = Json.format[Match]
  implicit val mpFormat = Json.format[MatchPlayer]
  implicit val mtFormat = Json.format[MatchTeam]
  implicit val placeFormat = Json.format[Place]
  implicit val pcFormat = Json.format[PlaceClub]
  implicit val playerFormat = Json.format[Player]
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
  implicit val substitutionFormat = Json.format[Substitution]
  implicit val teamFormat = Json.format[Team]
  implicit val typAnswerFormat = Json.format[TypAnswer]
  implicit val typCardFormat = Json.format[TypCard]
  implicit val typCompetitionFormat = Json.format[TypCompetition]
  implicit val typEventFormat = Json.format[TypEvent]
  implicit val typMatchFormat = Json.format[TypMatch]


  implicit def writes[A : Writes]: Writes[Page[A]] = (
    (__ \ 'items).write[Seq[A]] ~
      (__ \ 'page).write[Int] ~
      (__ \ 'offset).write[Long] ~
      (__ \ 'total).write[Long]
    ) (unlift(Page.unapply[A]))

  implicit val cityWithProvinceReads: Reads[(City, Province)] = (
    (__ \ 'city).read[City] ~
      (__ \ 'province).read[Province]
    ) tupled

  // or using the operators inspired by Scala parser combinators for those who know them
  implicit val cityWithProvinceWrites: Writes[(City, Province)] = (
    (__ \ 'city).write[City] ~
      (__ \ 'province).write[Province]
    ) tupled

  implicit val cardCompleteReads: Reads[(Card, Match, Team, Player, TypCard)] = (
    (__ \ 'card).read[Card] ~
      (__ \ 'match).read[Match] ~
      (__ \ 'team).read[Team] ~
      (__ \ 'player).read[Player] ~
      (__ \ 'typcard).read[TypCard]
    ) tupled

  implicit val cardCompleteWrites: Writes[(Card, Match, Team, Player, TypCard)] = (
    (__ \ 'card).write[Card] ~
      (__ \ 'match).write[Match] ~
      (__ \ 'team).write[Team] ~
      (__ \ 'player).write[Player] ~
      (__ \ 'typcard).write[TypCard]
    ) tupled

  implicit val eventCatCompleteReads: Reads[(EventCategory, Event, Category)] = (
    (__ \ 'eventcategory).read[EventCategory] ~
      (__ \ 'event).read[Event] ~
      (__ \ 'category).read[Category]
    ) tupled

  implicit val eventCatCompleteWrites: Writes[(EventCategory, Event, Category)] = (
    (__ \ 'eventcategory).write[EventCategory] ~
      (__ \ 'event).write[Event] ~
      (__ \ 'category).write[Category]
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

  implicit val eventCompleteReads: Reads[(Event, TypEvent, EventStatus)] = (
    (__ \ 'event).read[Event] ~
      (__ \ 'typevent).read[TypEvent] ~
      (__ \ 'eventstatus).read[EventStatus]
    ) tupled

  implicit val eventCompleteWrites: Writes[(Event, TypEvent, EventStatus)] = (
    (__ \ 'event).write[Event] ~
      (__ \ 'typevent).write[TypEvent] ~
      (__ \ 'eventstatus).write[EventStatus]
    ) tupled

  implicit val eventTeamCompleteReads: Reads[(EventTeam, Event, Team)] = (
    (__ \ 'eventteam).read[EventTeam] ~
      (__ \ 'event).read[Event] ~
      (__ \ 'team).read[Team]
    ) tupled

  implicit val eventTeamCompleteWrites: Writes[(EventTeam, Event, Team)] = (
    (__ \ 'eventteam).write[EventTeam] ~
      (__ \ 'event).write[Event] ~
      (__ \ 'team).write[Team]
    ) tupled

  implicit val goalCompleteReads: Reads[(Goal, Option[Player])] = (
    (__ \ 'goal).read[Goal] ~
      (__ \ 'striker).readNullable[Player]
    ) tupled

  implicit val goalCompleteWrites: Writes[(Goal, Option[Player])] = (
    (__ \ 'goal).write[Goal] ~
      (__ \ 'striker).write[Option[Player]]
    ) tupled



  implicit val answerCompleteReads: Reads[(Answer, Event, Player, TypAnswer)] = (
    (__ \ 'answer).read[Answer] ~
      (__ \ 'event).read[Event] ~
      (__ \ 'player).read[Player] ~
      (__ \ 'typAnswer).read[TypAnswer]
    ) tupled

  implicit val answerCompleteWrites: Writes[(Answer, Event, Player, TypAnswer)] = (
    (__ \ 'answer).write[Answer] ~
      (__ \ 'event).write[Event] ~
      (__ \ 'player).write[Player] ~
      (__ \ 'typAnswer).write[TypAnswer]
    ) tupled

  implicit val pcCompleteReads: Reads[(PlaceClub, Place, Club)] = (
    (__ \ 'placeclub).read[PlaceClub] ~
      (__ \ 'place).read[Place] ~
      (__ \ 'club).read[Club]
    ) tupled

  implicit val pcCompleteWrites: Writes[(PlaceClub, Place, Club)] = (
    (__ \ 'placeclub).write[PlaceClub] ~
      (__ \ 'place).write[Place] ~
      (__ \ 'club).write[Club]
    ) tupled

  implicit val mpCompleteReads: Reads[(MatchPlayer, Match, Player, Team)] = (
    (__ \ 'matchplayer).read[MatchPlayer] ~
      (__ \ 'match).read[Match] ~
      (__ \ 'player).read[Player] ~
      (__ \ 'team).read[Team]
    ) tupled

  implicit val mpCompleteWrites: Writes[(MatchPlayer, Match, Player, Team)] = (
    (__ \ 'matchplayer).write[MatchPlayer] ~
      (__ \ 'match).write[Match] ~
      (__ \ 'player).write[Player] ~
      (__ \ 'team).write[Team]
    ) tupled

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

  implicit val subCompleteReads: Reads[(Substitution, Match, Team, Player, Player)] = (
    (__ \ 'substitution).read[Substitution] ~
      (__ \ 'match).read[Match] ~
      (__ \ 'team).read[Team] ~
      (__ \ 'playerin).read[Player] ~
      (__ \ 'playerout).read[Player]
    ) tupled

  implicit val subCompleteWrites: Writes[(Substitution, Match, Team, Player, Player)] = (
    (__ \ 'substitution).write[Substitution] ~
      (__ \ 'match).write[Match] ~
      (__ \ 'team).write[Team] ~
      (__ \ 'playerin).write[Player] ~
      (__ \ 'playerout).write[Player]
    ) tupled

}
