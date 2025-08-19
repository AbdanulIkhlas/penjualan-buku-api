package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, OFormat}
import java.time.LocalDateTime

/** Model transaksi pembelian.
  *
  * @param id
  *   ID unik transaksi.
  * @param cartId
  *   ID keranjang yang terkait dengan transaksi ini.
  * @param cartPrice
  *   Total harga keranjang saat transaksi dilakukan.
  * @param deliveryServicePrice
  *   Biaya layanan pengiriman.
  * @param totalPrice
  *   Total harga keseluruhan transaksi (cartPrice + deliveryServicePrice).
  * @param createdAt
  *   Waktu pembuatan transaksi.
  * @param updatedAt
  *   Waktu terakhir transaksi diperbarui.
  */
case class Transaction(
    id: Option[Long],
    cartId: Long,
    cartPrice: BigDecimal,
    deliveryServicePrice: BigDecimal,
    totalPrice: BigDecimal,
    createdAt: Option[LocalDateTime],
    updatedAt: Option[LocalDateTime]
)

object Transaction {
  // Implicit format untuk serialisasi/deserialisasi JSON
  implicit val format: OFormat[Transaction] = Json.format[Transaction]

  //Parser Anorm untuk mengonversi baris ResultSet menjadi objek Transaction.
  val parser: RowParser[Transaction] = {
    get[Option[Long]]("id") ~
      get[Long]("cart_id") ~
      get[BigDecimal]("cart_price") ~
      get[BigDecimal]("delivery_service_price") ~
      get[BigDecimal]("total_price") ~
      get[Option[LocalDateTime]]("created_at") ~
      get[Option[LocalDateTime]]("updated_at") map {
        case id ~ cartId ~ cartPrice ~ deliveryServicePrice ~ totalPrice ~ createdAt ~ updatedAt =>
          Transaction(id, cartId, cartPrice, deliveryServicePrice, totalPrice, createdAt, updatedAt)
      }
  }
}
