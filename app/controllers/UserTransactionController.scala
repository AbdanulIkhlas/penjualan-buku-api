package controllers

import javax.inject._
import play.api.mvc._
import repositories.UserTransactionRepository
import utils.ResponseHelper
import scala.concurrent.ExecutionContext

@Singleton
class UserTransactionController @Inject() (
    cc: ControllerComponents,
    userTransactionRepo: UserTransactionRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  // Endpoint untuk melihat semua transaksi user GET : /userTransactions/:userId
  def getUserTransactions(userId: Long) = Action.async { implicit request =>
    userTransactionRepo
      .getTransactionsByUser(userId)
      .map { transactions =>
        if (transactions.isEmpty)
          ResponseHelper.notFound(s"Tidak ada transaksi untuk user dengan ID $userId")
        else
          ResponseHelper.success(transactions, s"Daftar transaksi user ID $userId")
      }
      .recover { case ex: Throwable =>
        ResponseHelper.internalServerError(s"Terjadi kesalahan: ${ex.getMessage}")
      }
  }
}
