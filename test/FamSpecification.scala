import play.api.test.{PlaySpecification, FakeApplication}

/**
 * Created by gbougeard on 23/04/14.
 */
trait FamSpecification extends  PlaySpecification{

  def fakeAppMemDB = FakeApplication(additionalConfiguration = inMemoryDatabase())

  def fakeAppMemDBMinimal = FakeApplication(additionalConfiguration = inMemoryDatabase("default"), withoutPlugins = Seq("service.SlickUserService"))


}
