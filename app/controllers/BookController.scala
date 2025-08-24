package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import repositories.BookRepository
import utils.ControllerHelper
import scala.concurrent.ExecutionContext


/** Controller untuk mengelola operasi CRUD pada entitas Book
  *
  * @param cc
  *   Komponen Controller.
  * @param bookRepository
  *   Repository untuk interaksi database Book.
  * @param ec
  *   ExecutionContext untuk operasi asinkron.
  */
@Singleton
class BookController @Inject() (
    cc: ControllerComponents,
    bookRepository: BookRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

   // Menambahkan buku baru. Endpoint: POST /books
  def createBook: Action[JsValue] = Action.async(parse.json) { request =>
      ControllerHelper.addData(request = request, repository = bookRepository, entityName = "Buku")
  }

  // Update book yang sudah ada. Endpoint: PUT /books/:id
  def updateBook(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    println("masuk sini di buku")
    ControllerHelper.updateData(id = id, request = request, repository = bookRepository, entityName = "Book")
  }

  // Menghapus book temp berdasarkan ID. Endpoint: DELETE /books/:id
  def softDeleteBook(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.softDeleteData(id = id, repository = bookRepository, entityName = "Buku")
  }

  // Menghapus book secara permanen berdasarkan ID. Endpoint: DELETE /books/permanent/:id
  def deleteBook(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.deleteData(id = id, repository = bookRepository, entityName = "Buku")
  }

  // Mendapatkan semua book. Endpoint: GET /books
  def getAllBooks: Action[AnyContent] = Action.async {
    ControllerHelper.findAllData(repository = bookRepository, entityName = "Buku")
  }

  // Mendapatkan book berdasarkan ID. Endpoint: GET /books/:id
  def getBookById(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findByIdData(id = id, repository = bookRepository, entityName = "Buku")
  }


}
