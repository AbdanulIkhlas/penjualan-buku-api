package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import repositories.CartRepository
import utils.ControllerHelper
import scala.concurrent.ExecutionContext

/** Controller untuk mengelola operasi CRUD pada entitas Cart
 *
 * @param cc
 *   Komponen Controller.
 * @param cartRepository
 *   Repository untuk interaksi database Cart.
 * @param ec
 *   ExecutionContext untuk operasi asinkron.
 */

@Singleton
class CartController @Inject() (
    cc: ControllerComponents,
    cartRepository: CartRepository
)(implicit ec: ExecutionContext)
extends AbstractController(cc) {

  // Menambahkan cart baru. Endpoint: POST /cart
  def createCart: Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.addData(request = request, repository = cartRepository, entityName = "Keranjang")
  }

  // Memperbarui cart yang sudah ada. Endpoint: PUT /cart/:id
  def updateCart(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.updateData(id = id, request = request, repository = cartRepository, entityName = "Keranjang")
  }

  // Menghapus cart secara permanen berdasarkan ID. Endpoint: DELETE /cart/permanent/:id
  def deleteCart(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.deleteData(id = id, repository = cartRepository, entityName = "Keranjang")
  }

  // Menghapus cart temp berdasarkan ID. Endpoint: DELETE /cart/:id
  def softDeleteCart(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.softDeleteData(id = id, repository = cartRepository, entityName = "Keranjang")
  }

  // Mendapatkan semua cart. Endpoint: GET /cart
  def getAllCarts: Action[AnyContent] = Action.async {
    ControllerHelper.findAllData(repository = cartRepository, entityName = "Keranjang")
  }

  // Mendapatkan cart berdasarkan ID. Endpoint: GET /cart/:id
  def getCartById(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findByIdData(id = id, repository = cartRepository, entityName = "Keranjang")
  }

  // Mendapatkan cart berdasarkan ID User. Endpoint: GET /cart/id-user/:id
  def getCartByIdUser(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findByIdUser(idUser = id, repository = cartRepository, entityName = "Keranjang")
  }
}