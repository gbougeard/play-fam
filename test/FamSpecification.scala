import play.api.mvc.AnyContentAsEmpty
import play.api.test.{FakeRequest, FakeHeaders, PlaySpecification, FakeApplication}
import play.api.http._

/**
 * Created by gbougeard on 23/04/14.
 */
trait FamSpecification extends  PlaySpecification{

  def fakeAppMemDB = FakeApplication(additionalConfiguration = inMemoryDatabase())

  def fakeAppMemDBMinimal = FakeApplication(additionalConfiguration = inMemoryDatabase("default"), withoutPlugins = Seq("service.SlickUserService"))

  val jsonHeaders = FakeHeaders(Seq(HeaderNames.ACCEPT -> Seq(MimeTypes.JSON)))

  def fakeGETasJson(url : String) = FakeRequest(method = "GET", uri = url, headers = jsonHeaders, body = AnyContentAsEmpty)
}
