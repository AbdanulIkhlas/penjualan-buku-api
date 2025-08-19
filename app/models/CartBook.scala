package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, OFormat}

/** Model untuk relasi antara keranjang dan buku.
    *
    * @param id
    *   ID unik relasi keranjang-buku.
    * @param cartId
    *   ID keranjang yang terkait dengan relasi ini.
    * @param bookId
    *   ID buku yang terkait dengan relasi ini.
    * @param qty
    *   Jumlah buku dalam keranjang.
    * @param unitPrice
    *   Harga satuan buku dalam keranjang.
    * @param totalPrice
    *   Total harga keseluruhan buku dalam keranjang.
    */
case class CartBook(
    id: Option[Long],
    cartId: Long,
    bookId: Long,
    qty: Int,
    unitPrice: BigDecimal,
    totalPrice: BigDecimal
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
      get[BigDecimal]("total_price") map { case id ~ cartId ~ bookId ~ qty ~ unitPrice ~ totalPrice =>
        CartBook(id, cartId, bookId, qty, unitPrice, totalPrice)
      }
  }
}
