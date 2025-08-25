package repositories

import models.Transaction
import javax.inject.{Inject, Singleton}
import play.api.db.Database
import scala.concurrent.{ExecutionContext, Future}
import utils.DatabaseHelper
import scala.collection.immutable.ListMap
import java.time.LocalDateTime

// repository untuk operasi database pada tabel Transaction
@Singleton
class TransactionRepository @Inject() (db: Database, dbHelper: DatabaseHelper)(implicit ec: ExecutionContext)
    extends BaseRepository[Transaction] {

  // buat transaksi
  override def create(transaction: Transaction): Future[Transaction] = {
    val data = ListMap(
      "cart_id"                -> transaction.cart_id,
      "cart_price"             -> transaction.cart_price,
      "delivery_service_price" -> transaction.delivery_service_price,
      "total_price"            -> transaction.total_price,
      "created_at"             -> LocalDateTime.now(),
      "updated_at"             -> LocalDateTime.now()
    )

    dbHelper.insertAndReturnId("transactions", data).map { id =>
      transaction.copy(id = Some(id))
    }
  }

  // update transaksi
  override def update(id: Long, transaction: Transaction): Future[Int] = {
    dbHelper.updateRowById(
      tableName = "transactions",
      data = ListMap(
        "cart_id"                -> transaction.cart_id,
        "cart_price"             -> transaction.cart_price,
        "delivery_service_price" -> transaction.delivery_service_price,
        "total_price"            -> transaction.total_price,
        "created_at"             -> transaction.created_at,
        "updated_at"             -> LocalDateTime.now()
      ),
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_transactions"
    )
  }

  // soft delete transaksi
  override def softDelete(id: Long): Future[Int] = {
    dbHelper.softDeleteRowById(
      tableName = "transactions",
      idColumn = "id",
      idValue = id,
      softDeleteColumnName = "is_delete_transactions"
    )
  }

  // delete permanent transaksi
  override def delete(id: Long): Future[Int] = {
    dbHelper.deletePermanentRowById(
      tableName = "transactions",
      idColumn = "id",
      idValue = id
    )
  }

  // tampilkan semua transaksi
  override def findAll(): Future[Seq[Transaction]] = {
    dbHelper.findAll[Transaction](
      table = "transactions",
      parser = Transaction.parser,
      condition = Some("is_delete_transactions = FALSE")
    )
  }

  // tampilkan transaksi berdasarkan id
  override def findById(id: Long): Future[Option[Transaction]] = {
    dbHelper.findByIdRow[Transaction](
      tableName = "transactions",
      idColumn = "id",
      idValue = id,
      parser = Transaction.parser
    )
  }
}
