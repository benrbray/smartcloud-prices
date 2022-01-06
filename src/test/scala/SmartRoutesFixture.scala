import org.http4s._

import cats.implicits._
import cats.effect._

import munit.CatsEffectSuite

import _root_.prices._
import _root_.prices.data.PriceInfo

import mock.MockClientService

////////////////////////////////////////////////////////////

trait SmartRoutesFixture extends CatsEffectSuite {

  /**
  * Suites should define data to be served by the mock ClientService.
  */
  def mockData: List[PriceInfo] 

  /**
  * Suites can test routes defined by SmartApp.
  */
  val routes = ResourceSuiteLocalFixture(
    "routes",
    Resource.make
      // acquire
      ({
        val mockClientService = MockClientService.make[IO](mockData)
        val route = SmartApp.make[IO](mockClientService)
        route.pure[IO]
      })
      // release
      (_ => IO.unit)
  )

  /**
  * Returns TRUE if the actual decoded response and status match the expected values.
  * Adapted from https://http4s.org/v0.18/testing/.
  */
  def check[A](
      actualResp:     Response[IO], 
      expectedStatus: Status, 
      expectedBody:   Option[A])(
    implicit ev: EntityDecoder[IO, A]
  ): Boolean = {
    val statusCheck        = actualResp.status == expectedStatus 
    val bodyCheck          = expectedBody.fold[Boolean](
        actualResp.body.compile.toVector.unsafeRunSync().isEmpty)( // Verify Response's body is empty.
        expected => actualResp.as[A].unsafeRunSync() == expected
    )
    statusCheck && bodyCheck   
  }
  override def munitFixtures = List(routes)

}