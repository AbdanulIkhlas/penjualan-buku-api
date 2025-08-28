package repositories

import javax.inject.Inject
import models.UserTransaction
import utils.DatabaseHelper
import scala.concurrent.{ExecutionContext, Future}
import anorm.SQL

class UserTransactionRepository @Inject()(
                                           dbHelper: DatabaseHelper
                                         )(implicit ec: ExecutionContext) {

  /** Ambil semua transaksi user beserta daftar buku */
  def getTransactionsByUser(userId: Long): Future[Seq[UserTransaction]] = {
    val sqlQuery =
      """
        SELECT t.id AS transaction_id, t.cart_id, t.cart_price, t.delivery_service_price, t.total_price,
               cb.id AS cart_book_id, cb.qty AS quantity, cb.unit_price, cb.total_price AS book_total_price,
               b.id AS book_id, b.title, b.author, b.price AS book_price
        FROM transactions t
        JOIN cart c ON t.cart_id = c.id
        JOIN cart_books cb ON cb.cart_id = c.id
        JOIN books b ON b.id = cb.book_id
        WHERE c.user_id = {userId}
        ORDER BY t.created_at DESC
      """

    dbHelper.withConnection { implicit conn =>
      // Parsing langsung menggunakan parser gabungan
      val rawData: Seq[(UserTransaction, models.UserTransactionBook)] =
        SQL(sqlQuery)
          .on("userId" -> userId)
          .as(UserTransaction.transactionWithBookParser.*)

      // Grouping berdasarkan transactionId agar JSON rapi
      val grouped: Seq[UserTransaction] = rawData
        .groupBy(_._1.transactionId)
        .map { case (_, trxBooks) =>
          val firstTrx = trxBooks.head._1
          val books = trxBooks.map(_._2)
          firstTrx.copy(books = books)
        }
        .toSeq

      grouped
    }
  }
}
