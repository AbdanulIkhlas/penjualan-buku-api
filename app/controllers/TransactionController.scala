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

  // Menambahkan transaction baru. Endpoint: POST /transaction
  def createTransaction: Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.addData(request = request, repository = transactionRepository, entityName = "Transaksi")
  }

  // Memperbarui transaction yang sudah ada. Endpoint: PUT /transaction/:id
  def updateTransaction(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.updateData(id = id, request = request, repository = transactionRepository, entityName = "Transaksi")
  }

  // Menghapus transaction secara permanen berdasarkan ID. Endpoint: DELETE /transaction/permanent/:id
  def deleteTransaction(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.deleteData(id = id, repository = transactionRepository, entityName = "Transaksi")
  }

  // Menghapus transaction temp berdasarkan ID. Endpoint: DELETE /transaction/:id
  def softDeleteTransaction(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.softDeleteData(id = id, repository = transactionRepository, entityName = "Transaksi")
  }

  // Mendapatkan semua transaction. Endpoint: GET /transaction
  def getAllTransactions: Action[AnyContent] = Action.async {
    ControllerHelper.findAllData(repository = transactionRepository, entityName = "Transaksi")
  }

  // Mendapatkan transaction berdasarkan ID. Endpoint: GET /transaction/:id
  def getTransactionById(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findByIdData(id = id, repository = transactionRepository, entityName = "Transaksi")
  }
}
