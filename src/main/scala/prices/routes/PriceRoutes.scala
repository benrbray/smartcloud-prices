package prices.routes

import cats.implicits._
import cats.effect._
import org.http4s.HttpRoutes
import org.http4s.circe._
import org.http4s.dsl.Http4sDsl
import org.http4s.server.Router

import prices.routes.protocol._
import prices.services.PriceService

final case class PriceRoutes[F[_]: Sync](priceService: PriceService[F]) extends Http4sDsl[F] {

  val prefix = "/prices"

  implicit val pricesResponseEncoder = jsonEncoderOf[F, List[PriceResponse]]

  private val get: HttpRoutes[F] = HttpRoutes.of {
    case GET -> Root =>
      priceService.getAll().flatMap(kinds => Ok(kinds.map(k => PriceResponse(k))))
  }

  def routes: HttpRoutes[F] =
    Router(
      prefix -> get
    )

}
