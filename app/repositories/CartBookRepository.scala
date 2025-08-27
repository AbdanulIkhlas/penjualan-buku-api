package repositories

import models.CartBook
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import scala.concurrent.{ExecutionContext, Future}
import utils.DatabaseHelper
import scala.collection.immutable.ListMap
import java.time.LocalDateTime

// repository untuk operasi database pada tabel CartBook
@Singleton
class CartBookRepository @Inject() (db: Database, dbHelper: DatabaseHelper)(implicit ec: ExecutionContext)
    extends BaseRepository[CartBook] {
  val bookRepository = new BookRepository(db, dbHelper)
  val cartRepository = new CartRepository(db, dbHelper)

  // tambah cart book
  override def create(cartBook: CartBook): Future[CartBook] = {
    // Ambil data buku berdasarkan book_id
    bookRepository.findById(cartBook.book_id).flatMap {
      case Some(book) =>
        // Cek stok
        if (book.stock >= cartBook.qty) {
          val unitPrice = book.price
          val totalPrice = unitPrice * cartBook.qty

          val data = ListMap(
            "cart_id"     -> cartBook.cart_id,
            "book_id"     -> cartBook.book_id,
            "qty"         -> cartBook.qty,
            "unit_price"  -> unitPrice,
            "total_price" -> totalPrice
          )

          // Insert ke cart_books
          dbHelper.insertAndReturnId("cart_books", data).flatMap { id =>
            // Update stok buku
            bookRepository.updateStock(cartBook.book_id, cartBook.qty, "mines").flatMap { _ =>
              // Ambil data cart lama
              cartRepository.findById(cartBook.cart_id).flatMap {
                case Some(cart) =>
                  val newPrice = cart.price + totalPrice
                  val updatedCart = cart.copy(price = newPrice, updatedAt = Some(LocalDateTime.now()))
                  cartRepository.update(cart.id.get, updatedCart).map(id =>
                    cartBook.copy(id = Some(id))
                  )
                case None =>
                  Future.failed(new Exception(s"Cart dengan id ${cartBook.cart_id} tidak ditemukan"))
              }
            }
          }
        } else {
          Future.failed(new Exception("Stok buku tidak cukup"))
        }
      case None =>
        Future.failed(new Exception("Buku tidak ditemukan"))
    }
  }


  // update cart book
  override def update(id: Long, cartBook: CartBook): Future[Int] = {
    // Ambil data lama
    dbHelper.findByIdRow[CartBook](
      tableName = "cart_books",
      idColumn = "id",
      idValue = id,
      parser = CartBook.parser
    ).flatMap {
      case Some(oldCartBook) =>
        println("old cart book : ", oldCartBook)
        println("new cart book : ", cartBook)
        val checkQty = cartBook.qty - oldCartBook.qty
        var mode = ""
        var newStock = 0

        if (checkQty > 0) {
          mode = "mines"
          newStock = checkQty
        } else if (checkQty < 0) {
          mode = "plus"
          newStock = -checkQty
        }else if (checkQty == 0) {
          mode = "same"
          newStock = 0
        }

        // cek stok sebelum mengurangi
        bookRepository.isStockAvailable(cartBook.book_id, checkQty).flatMap { available =>
          if (!available) Future.failed(new Exception("Stok buku tidak cukup"))
          else {
            // update cart_books
            println("unit price " + oldCartBook.unit_price)
            println("unit qty " + cartBook.qty)
            dbHelper.updateRowById(
              tableName = "cart_books",
              data = ListMap(
                "cart_id"     -> cartBook.cart_id,
                "book_id"     -> cartBook.book_id,
                "qty"         -> cartBook.qty,
                "unit_price"  -> oldCartBook.unit_price,
                "total_price" -> oldCartBook.unit_price * cartBook.qty
              ),
              idColumn = "id",
              idValue = id,
              softDeleteColumnName = "is_delete_cart_books"
            ).flatMap { updatedRows =>
              // update stok buku: kurangi
              bookRepository.updateStock(cartBook.book_id, newStock, mode).map(_ => updatedRows)
            }
          }
        }

      case None =>
        Future.failed(new Exception("CartBook tidak ditemukan"))
    }
  }


  // soft delete cart book
  override def softDelete(id: Long): Future[Int] = {
    // Ambil dulu data cartBook berdasarkan id
    dbHelper.findByIdRow[CartBook](
      tableName = "cart_books",
      idColumn = "id",
      idValue = id,
      parser = CartBook.parser
    ).flatMap {
      case Some(cartBook) =>
        // Delete baris cart_books temp
        dbHelper.softDeleteRowById(
          tableName = "cart_books",
          idColumn = "id",
          idValue = id,
          softDeleteColumnName = "is_delete_cart_books"
        ).flatMap { deletedRows =>
          if (deletedRows > 0) {
            // Update stok buku dengan menambahkan qty yang dihapus
            bookRepository.updateStock(cartBook.book_id, cartBook.qty, "plus").map { _ =>
              deletedRows
            }
          } else {
            Future.successful(0)
          }
        }
      case None =>
        Future.failed(new Exception("CartBook tidak ditemukan"))
    }


  }

  // delete permanen
  override def delete(id: Long): Future[Int] = {
    // Ambil dulu data cartBook berdasarkan id
    dbHelper.findByIdRow[CartBook](
      tableName = "cart_books",
      idColumn = "id",
      idValue = id,
      parser = CartBook.parser
    ).flatMap {
      case Some(cartBook) =>
        // Delete baris cart_books permanen
        dbHelper.deletePermanentRowById(
          tableName = "cart_books",
          idColumn = "id",
          idValue = id
        ).flatMap { deletedRows =>
          if (deletedRows > 0) {
            // Update stok buku dengan menambahkan qty yang dihapus
            bookRepository.updateStock(cartBook.book_id, cartBook.qty, "plus").map { _ =>
              deletedRows
            }
          } else {
            Future.successful(0)
          }
        }
      case None =>
        Future.failed(new Exception("CartBook tidak ditemukan"))
    }
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

  // tampilkan keranjang berdasarkan id user
  override def findCartBookByIdCart(id: Long): Future[Seq[CartBook]] = {
    dbHelper.findAll[CartBook](
      table = "cart_books",
      parser = CartBook.parser,
      condition = Some(s"cart_id = $id")
    )
  }
}
