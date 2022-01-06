package prices.routes

import cats.implicits._
import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

import prices.routes.protocol._
import prices.services.ClientService

////////////////////////////////////////////////////////////

final case class PriceRoutes[F[_]: Sync](clientService: ClientService[F]) extends Http4sDsl[F] {

  val prefix = "/prices"

  implicit val pricesResponseEncoder = jsonEncoderOf[F, PriceResponse]

  private val get: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root :? KindQueryParamMatcher(kind) =>
      clientService.price(kind).flatMap(k => Ok(PriceResponse(k.kind, k.price)))
  }

  def routes: HttpRoutes[F] =
    Router(
      prefix -> get
    )

}
