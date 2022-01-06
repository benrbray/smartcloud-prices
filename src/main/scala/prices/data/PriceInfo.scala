package prices.data

////////////////////////////////////////////////////////////

/**
 * Data returned by SmartCloud /instances/<name> endpoint.
 */
final case class PriceInfo(kind: String, price: Double, timestamp: String)
