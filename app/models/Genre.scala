package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, OFormat}

/** Model genre buku.
  *
  * @param id
  *   ID unik genre.
  * @param name
  *   Nama genre.
  * @param description
  *   Deskripsi genre (opsional).
  */
case class Genre(
    id: Option[Long],
    name: String,
    description: Option[String]
)

object Genre {
  // Implicit format untuk serialisasi/deserialisasi JSON
  implicit val format: OFormat[Genre] = Json.format[Genre]

  // Parser Anorm untuk mengonversi row ResultSet menjadi objek Genre.
  val parser: RowParser[Genre] = {
    get[Option[Long]]("id") ~
      get[String]("name") ~
      get[Option[String]]("description") map { case id ~ name ~ description =>
        Genre(id, name, description)
      }
  }
}

// ------------------------------------------------------------------------------

//case class AllBodyGenre(
//    id: Option[Long],
//    name: String,
//    description: Option[String],
//    isDelete: Boolean = false
//)
//
//object AllBodyGenre {
//  // Implicit format untuk serialisasi/deserialisasi JSON
//  implicit val format: OFormat[Genre] = Json.format[Genre]
//
//  // Parser Anorm untuk mengonversi row ResultSet menjadi objek Genre.
//  val parser: RowParser[AllBodyGenre] = {
//    get[Option[Long]]("id") ~
//      get[String]("name") ~
//      get[Option[String]]("description") ~
//      get[Boolean]("is_delete_genres") map { case id ~ name ~ description ~ isDelete =>
//        AllBodyGenre(id, name, description, isDelete)
//      }
//  }
//}
