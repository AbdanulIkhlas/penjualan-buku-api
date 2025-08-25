package repositories

import models.CartBook
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import scala.concurrent.{ExecutionContext, Future}
import utils.DatabaseHelper
import scala.collection.immutable.ListMap

// repository untuk operasi database pada tabel CartBook
@Singleton
class CartBookRepository @Inject() (db: Database, dbHelper: DatabaseHelper)(implicit ec: ExecutionContext)
    extends BaseRepository[CartBook] {

  // tambah cart book
  override def create(cartBook: CartBook): Future[CartBook] = {
    val data = ListMap(
      "cart_id"     -> cartBook.cart_id,
      "book_id"     -> cartBook.book_id,
      "qty"         -> cartBook.qty,
      "unit_price"  -> cartBook.unit_price,
      "total_price" -> cartBook.total_price
    )

    dbHelper.insertAndReturnId("cart_books", data).map { id =>
      cartBook.copy(id = Some(id))
    }
  }

  // update cart book
  override def update(id: Long, cartBook: CartBook): Future[Int] = {
    dbHelper.updateRowById(
      tableName = "cart_books",
      data = ListMap(
        "cart_id"     -> cartBook.cart_id,
        "book_id"     -> cartBook.book_id,
        "qty"         -> cartBook.qty,
        "unit_price"  -> cartBook.unit_price,
        "total_price" -> cartBook.total_price
      ),
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_cart_books"
    )
  }

  // soft delete cart book
  override def softDelete(id: Long): Future[Int] = {
    dbHelper.softDeleteRowById(
      tableName = "cart_books",
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_cart_books"
    )
  }

  // delete permanen
  override def delete(id: Long): Future[Int] = {
    dbHelper.deletePermanentRowById(
      tableName = "cart_books",
      idColumn = "id",
      idValue = id
    )
  }

  // tampilkan semua cart book
  override def findAll(): Future[Seq[CartBook]] = {
    dbHelper.findAll[CartBook](
      table = "cart_books",
      parser = CartBook.parser,
      condition = Some("is_delete_cart_books = FALSE")
    )
  }

  // tampilkan cart book berdasarkan id
  override def findById(id: Long): Future[Option[CartBook]] = {
    dbHelper.findByIdRow[CartBook](
      tableName = "cart_books",
      idColumn = "id",
      idValue = id,
      parser = CartBook.parser
    )
  }
}
