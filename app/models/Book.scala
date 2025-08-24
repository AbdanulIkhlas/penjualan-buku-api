package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, OFormat}

/** Model buku.
  *
  * @param id
  *   ID unik buku.
  * @param title
  *   Judul buku.
  * @param author
  *   Penulis buku.
  * @param genreId
  *   ID genre buku.
  * @param description
  *   Deskripsi buku (opsional).
  * @param price
  *   Harga buku.
  * @param imageUrl
  *   URL gambar buku (opsional).
  * @param stock
  *   Stok buku.
  */
case class Book(
    id: Option[Long],
    title: String,
    author: String,
    genre_id: Long,
    description: Option[String],
    price: BigDecimal,
    image_url: Option[String],
    stock: Int
)

object Book {
  // Implicit format untuk serialisasi/deserialisasi JSON
  implicit val format: OFormat[Book] = Json.format[Book]

  // Parser Anorm untuk mengonversi baris ResultSet menjadi objek Book.
  val parser: RowParser[Book] = {
    get[Option[Long]]("id") ~
      get[String]("title") ~
      get[String]("author") ~
      get[Long]("genre_id") ~
      get[Option[String]]("description") ~
      get[BigDecimal]("price") ~
      get[Option[String]]("image_url") ~
      get[Int]("stock") map { case id ~ title ~ author ~ genre_id ~ description ~ price ~ image_url ~ stock =>
        Book(id, title, author, genre_id, description, price, image_url, stock)
      }
  }
}
