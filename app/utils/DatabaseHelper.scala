package utils

import play.api.db.Database
import anorm._
import anorm.SqlParser._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

/** Manajemen koneksi dan transaksi database.
  * @param db
  *   Instans Play Database.
  * @param ec
  *   ExecutionContext untuk operasi asinkron.
  */
class DatabaseHelper @Inject() (db: Database)(implicit ec: ExecutionContext) {

  /** Menjalankan operasi database dalam sebuah koneksi.
    *
    * @param block
    *   Blok kode yang akan dijalankan dengan koneksi database.
    * @tparam A
    *   Tipe hasil dari blok kode.
    * @return
    *   Future yang berisi hasil dari blok kode.
    */
  def withConnection[A](block: java.sql.Connection => A): Future[A] = Future {
    db.withConnection(block)
  }

  /** Menjalankan operasi database dalam sebuah transaksi. Jika ada kesalahan, transaksi akan di-rollback.
    *
    * @param block
    *   Blok kode yang akan dijalankan dengan koneksi database dalam transaksi.
    * @tparam A
    *   Tipe hasil dari blok kode.
    * @return
    *   Future yang berisi hasil dari blok kode.
    */
  def withTransaction[A](block: java.sql.Connection => A): Future[A] = Future {
    db.withTransaction(block)
  }
}
