package repositories

import models.Genre
import anorm._
import anorm.SqlParser._
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import scala.concurrent.{ExecutionContext, Future}
import utils.DatabaseHelper

/** Repository untuk operasi database pada tabel Genre.
  * @param db
  *   Instans Play Database.
  * @param ec
  *   ExecutionContext untuk operasi asinkron.
  * @param dbHelper
  *   Instans DatabaseHelper untuk manajemen koneksi/transaksi.
  */
@Singleton
class GenreRepository @Inject() (db: Database, dbHelper: DatabaseHelper)(implicit ec: ExecutionContext) {

  /** Tambah genre baru.
    * @param genre
    *   Objek Genre yang akan ditambahkan (id harus None).
    * @return
    *   Future yang berisi objek Genre yang telah ditambahkan dengan ID yang dihasilkan.
    */
//  def create(genre: Genre): Future[Genre] = dbHelper.withConnection { implicit connection =>
//    val id = SQL"""
//      INSERT INTO genres (name, description) VALUES (${genre.name}, ${genre.description})
//    """
//      .executeInsert(SqlParser.scalar[Long].single)
//    genre.copy(id = Some(id))
//  }
  def create(genre: Genre): Future[Genre] = {
    val data = Map(
      "name"             -> genre.name,
      "description"      -> genre.description,
      "is_delete_genres" -> false
    )

    dbHelper.insertAndReturnId("genres", data).map { id =>
      genre.copy(id = Some(id))
    }
  }

  /** Update genre
    * @param id
    *   ID genre yang akan diperbarui.
    * @param genre
    *   Objek Genre dengan data yang diperbarui.
    * @return
    *   Future yang berisi jumlah baris yang terpengaruh (1 jika berhasil, 0 jika tidak ditemukan).
    */
//  def update(id: Long, genre: Genre): Future[Int] = dbHelper.withTransaction { implicit connection =>
//    SQL"""
//      UPDATE genres SET name = ${genre.name}, description = ${genre.description} WHERE id = $id
//    """
//      .executeUpdate()
//  }
  def update(id: Long, genre: Genre): Future[Int] = {
    dbHelper.updateRowById(
      tableName = "genres",
      data = Map(
        "name"        -> genre.name,
        "description" -> genre.description
      ),
      idColumn = "id",
      idValue = id
    )
  }

  /** Hapus genre berdasarkan ID.
    *
    * @param id
    *   ID genre yang akan dihapus.
    * @return
    *   Future yang berisi jumlah baris yang terpengaruh (1 jika berhasil, 0 jika tidak ditemukan).
    */
//  def softDelete(id: Long): Future[Int] = dbHelper.withTransaction { implicit connection =>
//    SQL"UPDATE genres SET is_delete_genres = TRUE WHERE id = $id"
//      .executeUpdate()
//  }
  def softDelete(id: Long): Future[Int] = {
    print(s"[DEBUG] softDelete jalan di Genre id: $id")
    dbHelper.softDeleteRowById(
      tableName = "genres",
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_genres"
    )
  }

  /** Hapus genre permanen berdasarkan ID.
    *
    * @param id
    *   ID genre yang akan dihapus.
    * @return
    *   Future yang berisi jumlah baris yang terpengaruh (1 jika berhasil, 0 jika tidak ditemukan).
    */
  def delete(id: Long): Future[Int] = dbHelper.withTransaction { implicit connection =>
    SQL"DELETE FROM genres WHERE id = $id"
      .executeUpdate()
  }

  /** Menemukan semua genre.
    *
    * @return
    *   Future yang berisi daftar objek Genre.
    */
//  def findAll(): Future[Seq[Genre]] = dbHelper.withConnection { implicit connection =>
//    SQL"SELECT id, name, description FROM genres WHERE is_delete_genres = FALSE"
//      .as(Genre.parser.*)
//  }
  def findAll(): Future[Seq[Genre]] = {
    dbHelper.findAll[Genre](
      table = "genres",
      parser = Genre.parser,
      condition = Some("is_delete_genres = FALSE")
    )
  }

  /** Menemukan genre berdasarkan ID.
    *
    * @param id
    *   ID genre.
    * @return
    *   Future yang berisi Option[Genre], None jika tidak ditemukan.
    */
  def findById(id: Long): Future[Option[Genre]] = dbHelper.withConnection { implicit connection =>
    SQL"SELECT id, name, description FROM genres WHERE id = $id"
      .as(Genre.parser.singleOpt)
  }
}
