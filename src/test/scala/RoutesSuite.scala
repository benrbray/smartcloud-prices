import cats.effect._

import org.http4s._
import org.http4s.circe._
import org.http4s.implicits._

import io.circe.generic.auto._

import munit.CatsEffectSuite

import _root_.prices.data._
import _root_.prices.routes.protocol._

////////////////////////////////////////////////////////////

/**
 * Tests basic functionality of SmartApp routes with dummy data.
 */
class RoutesSuite extends CatsEffectSuite with SmartRoutesFixture {

  implicit val instanceKindsEntityDecoder: EntityDecoder[IO, List[InstanceKind]] = jsonOf[IO, List[InstanceKind]]

  override def mockData: List[PriceInfo] = List(
      PriceInfo("aaa", 0.2, "N/A"),
      PriceInfo("bbb", 0.4, "N/A"),
      PriceInfo("ccc", 0.6, "N/A")
    )

  test("/instance-kinds endpoint should return exact list of instance names") {
    val uri = uri"/instance-kinds"
    val req = Request[IO](method = Method.GET, uri = uri)

    // todo: test should allow data to appear in any order
    routes()
      .run(req)
      .map(check(_, Status.Ok, Some(mockData.map(info => InstanceKind(info.kind)))))
  }

  test("/prices endpoint should return original data") {
    val uri = uri"/prices".withQueryParam("kind", "aaa")
    val req = Request[IO](method = Method.GET, uri = uri)

    routes()
      .run(req)
      .map(check(_, Status.Ok, Some(PriceResponse("aaa", 0.2))))
  }
}