package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, OFormat}
import java.time.LocalDateTime

/** Model transaksi pembelian.
  *
  * @param id
  *   ID unik transaksi.
  * @param cart_id
  *   ID keranjang yang terkait dengan transaksi ini.
  * @param cart_price
  *   Total harga keranjang saat transaksi dilakukan.
  * @param delivery_service_price
  *   Biaya layanan pengiriman.
  * @param total_price
  *   Total harga keseluruhan transaksi (cart_price + delivery_service_price).
  * @param created_at
  *   Waktu pembuatan transaksi.
  * @param updated_at
  *   Waktu terakhir transaksi diperbarui.
  */
case class Transaction(
    id: Option[Long],
    cart_id: Long,
    cart_price: BigDecimal,
    delivery_service_price: BigDecimal,
    total_price: BigDecimal,
    created_at: Option[LocalDateTime],
    updated_at: Option[LocalDateTime]
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
        case id ~ cart_id ~ cart_price ~ delivery_service_price ~ total_price ~ created_at ~ updated_at =>
          Transaction(id, cart_id, cart_price, delivery_service_price, total_price, created_at, updated_at)
      }
  }
}
