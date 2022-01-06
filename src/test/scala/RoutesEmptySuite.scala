import cats.effect._

import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._

import io.circe.generic.auto._

import munit.CatsEffectSuite

import _root_.prices.data._
import prices.routes.protocol.PriceResponse

////////////////////////////////////////////////////////////

/**
 * Tests basic functionality of SmartApp routes with empty data.
 */
class RoutesEmptySuite extends CatsEffectSuite with SmartRoutesFixture {

  implicit val instanceKindsEntityDecoder: EntityDecoder[IO, List[InstanceKind]] = jsonOf[IO, List[InstanceKind]]

  override def mockData: List[PriceInfo] = List()

  test("/instance-kinds endpoint should not fail with empty data") {
    val uri = uri"/instance-kinds"
    val req = Request[IO](method = Method.GET, uri = uri)

    // todo: test should not impose an expected ordering on the returned data
    routes()
      .run(req)
      .map(check(_, Status.Ok, Some(mockData.map(info => InstanceKind(info.kind)))))
  }


  test("/prices endpoint should fail gracefully for missing kind") {
    val uri = uri"/prices".withQueryParam("kind", "aaa")
    val req = Request[IO](method = Method.GET, uri = uri)

    // todo: this test currently expected to fail
    val nothing: Option[PriceResponse] = None

    routes()
      .run(req)
      .map(check(_, Status.Ok, nothing))
  }
}