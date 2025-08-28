package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import repositories.TransactionRepository
import utils.ControllerHelper
import scala.concurrent.ExecutionContext

/** Controller untuk mengelola operasi CRUD pada entitas Transactions
  *
  * @param cc
  *   Komponen Controller.
  * @param transactionRepository
  *   Repository untuk interaksi database Transactions.
  * @param ec
  *   ExecutionContext untuk operasi asinkron.
  */

@Singleton
class TransactionController @Inject() (
    cc: ControllerComponents,
    transactionRepository: TransactionRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  // Menambahkan transactionHistory baru. Endpoint: POST /transactionHistory
  def createTransaction: Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.addData(request = request, repository = transactionRepository, entityName = "Transaksi")
  }

  // Memperbarui transactionHistory yang sudah ada. Endpoint: PUT /transactionHistory/:id
  def updateTransaction(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.updateData(id = id, request = request, repository = transactionRepository, entityName = "Transaksi")
  }

  // Menghapus transactionHistory secara permanen berdasarkan ID. Endpoint: DELETE /transactionHistory/permanent/:id
  def deleteTransaction(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.deleteData(id = id, repository = transactionRepository, entityName = "Transaksi")
  }

  // Menghapus transactionHistory temp berdasarkan ID. Endpoint: DELETE /transactionHistory/:id
  def softDeleteTransaction(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.softDeleteData(id = id, repository = transactionRepository, entityName = "Transaksi")
  }

  // Mendapatkan semua transactionHistory. Endpoint: GET /transactionHistory
  def getAllTransactions: Action[AnyContent] = Action.async {
    ControllerHelper.findAllData(repository = transactionRepository, entityName = "Transaksi")
  }

  // Mendapatkan transactionHistory berdasarkan ID. Endpoint: GET /transactionHistory/:id
  def getTransactionById(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findByIdData(id = id, repository = transactionRepository, entityName = "Transaksi")
  }
}
