package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import repositories.CartBookRepository
import utils.ControllerHelper
import scala.concurrent.ExecutionContext

/** Controller untuk mengelola operasi CRUD pada entitas CartBook.
 *
 * @param cc
 *   Komponen Controller.
 * @param cartBookRepository
 *   Repository untuk interaksi database CartBook.
 * @param ec
 *   ExecutionContext untuk operasi asinkron.
 */

@Singleton
class CartBookController @Inject() (
    cc: ControllerComponents,
    cartBookRepository: CartBookRepository
)(implicit ec: ExecutionContext)
extends AbstractController(cc) {

  // Menambahkan cartBook baru. Endpoint: POST /cartBook
  def createCartBook: Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.addData(request = request, repository = cartBookRepository, entityName = "Keranjang Buku")
  }

  // Memperbarui cartBook yang sudah ada. Endpoint: PUT /cartBook/:id
  def updateCartBook(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.updateData(id = id, request = request, repository = cartBookRepository, entityName = "Keranjang Buku")
  }

  // Menghapus cartBook secara permanen berdasarkan ID. Endpoint: DELETE /cartBook/permanent/:id
  def deleteCartBook(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.deleteData(id = id, repository = cartBookRepository, entityName = "Keranjang Buku")
  }

  // Menghapus cartBook temp berdasarkan ID. Endpoint: DELETE /cartBook/:id
  def softDeleteCartBook(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.softDeleteData(id = id, repository = cartBookRepository, entityName = "Keranjang Buku")
  }

  // Mendapatkan semua cartBook. Endpoint: GET /cartBook
  def getAllCartBooks: Action[AnyContent] = Action.async {
    ControllerHelper.findAllData(repository = cartBookRepository, entityName = "Keranjang Buku")
  }

  // Mendapatkan cartBook berdasarkan ID. Endpoint: GET /cartBook/:id
  def getCartBookById(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findByIdData(id = id, repository = cartBookRepository, entityName = "Keranjang Buku")
  }

  // Mendapatkan cart berdasarkan ID User. Endpoint: GET /cartBook/id-cart/:id
  def getCartBookByIdCart(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findCartBookByIdCart(idCart = id, repository = cartBookRepository, entityName = "Keranjang")
  }
}