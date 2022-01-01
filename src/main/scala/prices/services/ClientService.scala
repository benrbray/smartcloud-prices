package prices.services

import scala.util.control.NoStackTrace

import prices.data._

trait ClientService[F[_]] {
  def instances(): F[List[InstanceKind]]
  def price(kind: InstanceKind): F[PriceInfo]
}

object ClientService {

  sealed trait Exception extends NoStackTrace
  object Exception {
    case class APICallFailure(message: String) extends Exception
  }

}
