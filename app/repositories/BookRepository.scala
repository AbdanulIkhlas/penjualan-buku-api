package repositories

import models.Book
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import scala.concurrent.{ExecutionContext, Future}
import utils.DatabaseHelper
import scala.collection.immutable.ListMap

// Repository untuk operasi database pada tabel Book
@Singleton
class BookRepository @Inject() (db: Database, dbHelper: DatabaseHelper)(implicit ec: ExecutionContext)
    extends BaseRepository[Book] {
  val genreRepository = new GenreRepository(db, dbHelper)

  // Tambah buku baru
  override def create(book: Book): Future[Book] = {
    genreRepository.findById(book.genre_id).flatMap {
      case Some(genre) => // jika Genre ada maka lanjut insert buku
        val data = ListMap(
          "title"       -> book.title,
          "author"      -> book.author,
          "genre_id"    -> book.genre_id,
          "description" -> book.description.getOrElse(""),
          "price"       -> book.price,
          "image_url"   -> book.image_url.getOrElse(""),
          "stock"       -> book.stock
        )

        dbHelper.insertAndReturnId("books", data).map { id =>
          book.copy(id = Some(id))
        }

      case None => // Genre tidak ada, return error
        Future.failed(new Exception(s"Genre dengan id ${book.genre_id} tidak ditemukan"))
    }
  }


  // Update buku
  override def update(id: Long, book: Book): Future[Int] = {
    dbHelper.updateRowById(
      tableName = "books",
      data = ListMap(
        "title"       -> book.title,
        "author"      -> book.author,
        "genre_id"    -> book.genre_id,
        "description" -> book.description,
        "price"       -> book.price,
        "image_url"   -> book.image_url,
        "stock"       -> book.stock
      ),
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_books"
    )
  }

  // soft delete buku
  override def softDelete(id: Long): Future[Int] = {
    dbHelper.softDeleteRowById(
      tableName = "books",
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_books"
    )
  }

  // delete permanent buku
  override def delete(id: Long): Future[Int] = {
    dbHelper.deletePermanentRowById(
      tableName = "books",
      idColumn = "id",
      idValue = id
    )
  }

  // tampilkan semua buku
  override def findAll(): Future[Seq[Book]] = {
    dbHelper.findAll[Book](
      table = "books",
      parser = Book.parser,
      condition = Some("is_delete_books = FALSE")
    )
  }

  // tampilkan buku berdasarkan id
  override def findById(id: Long): Future[Option[Book]] = {
    dbHelper.findByIdRow[Book](
      tableName = "books",
      idColumn = "id",
      idValue = id,
      parser = Book.parser,
      softDeleteColumnName = "is_delete_books"
    )
  }

  /** Mengecek apakah stok buku cukup untuk quantity tertentu
   *
   * @param bookId ID buku
   * @param qty Jumlah yang ingin dibeli / ditambahkan ke keranjang
   * @return Future[Boolean] true jika stok cukup, false jika stok tidak cukup
   */
  def isStockAvailable(bookId: Long, qty: Int): Future[Boolean] = {
    dbHelper.findByIdRow[Book]("books", "id", bookId, Book.parser, "is_delete_books").map {
      case Some(book) => book.stock >= qty
      case None       => false
    }
  }

  /** Mengurangi stok buku berdasarkan bookId dan qty yang dibeli
   *
   * @param bookId ID buku
   * @param qty Jumlah yang ingin dibeli / ditambahkan ke keranjang
   * @return Future[Int] jumlah stok yang tersisa
   */
  def updateStock(bookId: Long, qty: Int, mode: String): Future[Int] = {
    // Ambil stok saat ini dulu
    var newStock = 0
    findById(bookId).flatMap {
      case Some(book) =>
        if (mode == "mines") {
          newStock = book.stock - qty
        }else if (mode == "plus") {
          newStock = book.stock + qty
        }else if (mode == "same") {
          newStock = book.stock
        }

        if (newStock < 0) {
          Future.failed(new Exception("Stok buku tidak cukup untuk update"))
        } else {
          dbHelper.updateRowById(
            tableName = "books",
            data = ListMap("stock" -> newStock),
            idColumn = "id",
            idValue = bookId,
            softDeleteColumnName = "is_delete_books"
          )
        }
      case None =>
        Future.failed(new Exception(s"Buku dengan id $bookId tidak ditemukan"))
    }
  }


}
