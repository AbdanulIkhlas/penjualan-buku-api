package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, OFormat}

/** Model users
  *
  * @param id
  *   ID unik pengguna.
  * @param name
  *   Nama pengguna.
  * @param email
  *   Alamat email pengguna (unik).
  * @param cityId
  *   ID kota pengguna (opsional).
  * @param address
  *   Alamat lengkap pengguna (opsional).
  */
case class User(
    id: Option[Long],
    name: String,
    email: String,
    city_id: Option[String],
    address: Option[String]
)

object User {
  // Implicit format untuk serialisasi/deserialisasi JSON
  implicit val format: OFormat[User] = Json.format[User]

  // Parser Anorm untuk mengonversi baris ResultSet menjadi objek User.
  val parser: RowParser[User] = {
    get[Option[Long]]("id") ~
      get[String]("name") ~
      get[String]("email") ~
      get[Option[String]]("city_id") ~
      get[Option[String]]("address") map { case id ~ name ~ email ~ city_id ~ address =>
        User(id, name, email, city_id, address)
      }
  }
}
