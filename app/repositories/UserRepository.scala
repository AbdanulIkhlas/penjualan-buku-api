package repositories

import models.User
import anorm._
import anorm.SqlParser._
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import scala.concurrent.{ExecutionContext, Future}
import utils.DatabaseHelper

// Repository untuk operasi database pada tabel User.
@Singleton
class UserRepository @Inject() (db: Database, dbHelper: DatabaseHelper)(implicit ec: ExecutionContext)
    extends BaseRepository[User] {

  /** Tambah user baru.
    *
    * @param user
    *   Objek User yang akan ditambahkan (id dan is_delete_users None).
    * @return
    *   Future yang berisi objek User yang telah ditambahkan dengan ID yang dihasilkan.
    */
  override def create(user: User): Future[User] = {
    val data = Map(
      "name"    -> user.name,
      "email"   -> user.email,
      "city_id" -> user.city_id,
      "address" -> user.address
    )

    dbHelper.insertAndReturnId("users", data).map { id =>
      user.copy(id = Some(id))
    }
  }

  /** Update user
    * @param id
    *   ID user yang akan diperbarui.
    * @param user
    *   Objek User dengan data yang diperbarui.
    * @return
    *   Future yang berisi jumlah baris yang terpengaruh (1 jika berhasil, 0 jika tidak ditemukan).
    */
  override def update(id: Long, user: User): Future[Int] = {
    dbHelper.updateRowById(
      tableName = "users",
      data = Map(
        "name"    -> user.name,
        "email"   -> user.email,
        "city_id" -> user.city_id,
        "address" -> user.address
      ),
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_users"
    )
  }

  /** Hapus user (tidak permanen) berdasarkan ID.
    *
    * @param id
    *   ID user yang akan dihapus.
    * @return
    *   Future yang berisi jumlah baris yang terpengaruh (1 jika berhasil, 0 jika tidak ditemukan).
    */
  override def softDelete(id: Long): Future[Int] = {
    dbHelper.softDeleteRowById(
      tableName = "users",
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_users"
    )
  }

  /** Hapus user permanen berdasarkan ID.
    *
    * @param id
    *   ID user yang akan dihapus.
    * @return
    *   Future yang berisi jumlah baris yang terpengaruh (1 jika berhasil, 0 jika tidak ditemukan).
    */
  override def delete(id: Long): Future[Int] = {
    dbHelper.deletePermanentRowById(
      tableName = "users",
      idColumn = "id",
      idValue = id
    )
  }

  /** Tampilkan semua user.
    *
    * @return
    *   Future yang berisi daftar objek User.
    */
  override def findAll(): Future[Seq[User]] = {
    dbHelper.findAll[User](
      table = "users",
      parser = User.parser,
      condition = Some("is_delete_users = FALSE")
    )
  }

  /** Tampilkan user berdasarkan ID.
    *
    * @param id
    *   ID user.
    * @return
    *   Future yang berisi Option[User], None jika tidak ditemukan.
    */
  override def findById(id: Long): Future[Option[User]] = {
    dbHelper.findByIdRow[User](
      tableName = "users",
      idColumn = "id",
      idValue = id,
      parser = User.parser
    )
  }
}
