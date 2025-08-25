package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, OFormat}

/** Model untuk relasi antara keranjang dan buku.
    *
    * @param id
    *   ID unik relasi keranjang-buku.
    * @param cart_id
    *   ID keranjang yang terkait dengan relasi ini.
    * @param book_id
    *   ID buku yang terkait dengan relasi ini.
    * @param qty
    *   Jumlah buku dalam keranjang.
    * @param unit_price
    *   Harga satuan buku dalam keranjang.
    * @param total_price
    *   Total harga keseluruhan buku dalam keranjang.
    */
case class CartBook(
    id: Option[Long],
    cart_id: Long,
    book_id: Long,
    qty: Int,
    unit_price: BigDecimal,
    total_price: BigDecimal
)

object CartBook {
  // Implicit format untuk serialisasi/deserialisasi JSON
  implicit val format: OFormat[CartBook] = Json.format[CartBook]

  // Parser Anorm untuk mengonversi baris ResultSet menjadi objek CartBook.
  val parser: RowParser[CartBook] = {
    get[Option[Long]]("id") ~
      get[Long]("cart_id") ~
      get[Long]("book_id") ~
      get[Int]("qty") ~
      get[BigDecimal]("unit_price") ~
      get[BigDecimal]("total_price") map { case id ~ cart_id ~ book_id ~ qty ~ unit_price ~ total_price =>
        CartBook(id, cart_id, book_id, qty, unit_price, total_price)
      }
  }
}
