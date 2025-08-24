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

  // Tambah buku baru
  override def create(book: Book): Future[Book] = {
    val data = ListMap(
      "title"       -> book.title,
      "author"      -> book.author,
      "genre_id"    -> book.genre_id,
      "description" -> book.description.getOrElse(""),
      "price"       -> book.price,
      "image_url"   -> book.image_url.getOrElse(""),
      "stock"       -> book.stock
    )

    println("{DEBUGGING} type price : " + book.price.getClass)
    println("{DEBUGGING} price value : " + book.price)
    println("{DEBUGGING} title value : " + book.title)

    dbHelper.insertAndReturnId("books", data).map { id =>
      book.copy(id = Some(id))
    }
  }

  // Update buku
  override def update(id: Long, book: Book): Future[Int] = {
    dbHelper.updateRowById(
      tableName = "books",
      data = Map(
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
      parser = Book.parser
    )
  }

}
