package prices.services

import scala.util.control.NoStackTrace

import prices.data._

trait PriceService[F[_]] {
  def getPrice(kind: InstanceKind): F[PriceInfo]
}

object PriceService {

  sealed trait Exception extends NoStackTrace
  object Exception {
    case class APICallFailure(message: String) extends Exception
  }

}
