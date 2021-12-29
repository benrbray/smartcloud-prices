package prices.routes.protocol

import io.circe._
import io.circe.syntax._

import prices.data._

final case class PriceResponse(value: PriceInfo)

object PriceResponse {

  implicit val encoder: Encoder[PriceResponse] =
    Encoder.instance[PriceResponse] {
      case PriceResponse(k) =>
        Json.obj(
          "kind" -> k.kind.getString.asJson,
          "amount" -> k.amount.asJson
        )
    }

}
