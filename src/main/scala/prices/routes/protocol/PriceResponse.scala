package prices.routes.protocol

import io.circe._
import io.circe.syntax._

import prices.data._

final case class PriceResponse(value: InstanceKind)

object PriceResponse {

  implicit val encoder: Encoder[PriceResponse] =
    Encoder.instance[PriceResponse] {
      case PriceResponse(k) =>
        Json.obj(
          "kind" -> k.getString.asJson
        )
    }

}
