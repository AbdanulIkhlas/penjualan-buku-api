package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, OFormat}
import java.time.LocalDateTime

/** Model keranjang belanja.
    *
    * @param id
    *   ID unik keranjang belanja.
    * @param userId
    *   ID pengguna yang memiliki keranjang belanja.
    * @param price
    *   Total harga keranjang belanja.
    * @param status
    *   Status keranjang belanja (opsional).
    * @param createdAt
    *   Waktu pembuatan keranjang belanja (opsional).
    * @param updatedAt
    *   Waktu terakhir keranjang belanja diperbarui (opsional).
    */
case class Cart(
    id: Option[Long],
    userId: Long,
    price: BigDecimal,
    status: String, // 'active' or 'ordered'
    createdAt: Option[LocalDateTime],
    updatedAt: Option[LocalDateTime]
)

object Cart {
  // Implicit format untuk serialisasi/deserialisasi JSON
  implicit val format: OFormat[Cart] = Json.format[Cart]

  // Parser Anorm untuk mengonversi baris ResultSet menjadi objek Cart.
  val parser: RowParser[Cart] = {
    get[Option[Long]]("id") ~
      get[Long]("user_id") ~
      get[BigDecimal]("price") ~
      get[String]("status") ~
      get[Option[LocalDateTime]]("created_at") ~
      get[Option[LocalDateTime]]("updated_at") map { case id ~ userId ~ price ~ status ~ createdAt ~ updatedAt =>
        Cart(id, userId, price, status, createdAt, updatedAt)
      }
  }
}
