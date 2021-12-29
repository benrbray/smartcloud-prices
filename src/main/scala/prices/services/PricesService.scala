package prices.services

import scala.util.control.NoStackTrace

import prices.data._

trait PriceService[F[_]] {
  def getAll(): F[List[InstanceKind]]
}

object PriceService {

  sealed trait Exception extends NoStackTrace
  object Exception {
    case class APICallFailure(message: String) extends Exception
  }

}
