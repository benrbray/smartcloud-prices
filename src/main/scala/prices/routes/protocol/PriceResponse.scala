package prices.routes.protocol

import cats.effect._

import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._

import org.http4s._
import org.http4s.circe._

////////////////////////////////////////////////////////////

final case class PriceResponse(kind: String, amount: Double)

object PriceResponse {

  implicit val encoder: Encoder[PriceResponse] =
    Encoder.instance[PriceResponse] {
      case PriceResponse(kind, amt) =>
        Json.obj(
          "kind" -> kind.asJson,
          "amount" -> amt.asJson
        )
    }

  implicit val pricesEntityDecoder: EntityDecoder[IO, PriceResponse]
    = jsonOf[IO, PriceResponse]

}
