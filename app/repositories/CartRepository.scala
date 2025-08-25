package repositories

import models.Cart
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import scala.concurrent.{ExecutionContext, Future}
import utils.DatabaseHelper
import scala.collection.immutable.ListMap
import java.time.LocalDateTime

// repository untuk operasi database pada tabel Cart
@Singleton
class CartRepository @Inject() (db: Database, dbHelper: DatabaseHelper)(implicit ec: ExecutionContext)
extends BaseRepository[Cart] {

  // Buat keranjang baru
  override def create(cart: Cart): Future[Cart] = {
    val data = ListMap(
      "user_id"       -> cart.user_id,
      "price"         -> cart.price,
      "status"        -> "active",
      "created_at"    -> LocalDateTime.now(),
      "updated_at"    -> LocalDateTime.now()
    )


    dbHelper.insertAndReturnId("cart", data).map { id =>
      cart.copy(id = Some(id))
    }
  }

  // Update keranjang
  override def update(id: Long, cart: Cart): Future[Int] = {
    dbHelper.updateRowById(
      tableName = "cart",
      data = ListMap(
        "user_id"       -> cart.user_id,
        "price"         -> cart.price,
        "status"        -> cart.status,
        "created_at"    -> cart.createdAt,
        "updated_at"    -> LocalDateTime.now()
      ),
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_cart"
    )
  }

  // soft delete keranjang
  override def softDelete(id: Long): Future[Int] = {
    dbHelper.softDeleteRowById(
      tableName = "cart",
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_cart"
    )
  }

  // delete permanent keranjang
  override def delete(id: Long): Future[Int] = {
    dbHelper.deletePermanentRowById(
      tableName = "cart",
      idColumn = "id",
      idValue = id
    )
  }

  // tampilkan semua keranjang
  override def findAll(): Future[Seq[Cart]] = {
    dbHelper.findAll[Cart](
      table = "cart",
      parser = Cart.parser,
      condition = Some("is_delete_cart = FALSE")
    )
  }

  // tampilkan keranjang berdasarkan id
  override def findById(id: Long): Future[Option[Cart]] = {
    dbHelper.findByIdRow[Cart](
      tableName = "cart",
      idColumn = "id",
      idValue = id,
      parser = Cart.parser
    )
  }

  // tampilkan keranjang berdasarkan id user
  override def findByIdUser(id: Long): Future[Seq[Cart]] = {
    dbHelper.findAll[Cart](
      table = "cart",
      parser = Cart.parser,
      condition = Some(s"user_id = $id")
    )
  }
}