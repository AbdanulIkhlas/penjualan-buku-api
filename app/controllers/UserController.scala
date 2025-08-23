package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import repositories.UserRepository
import models.User
import utils.ResponseHelper
import utils.ControllerHelper
import scala.concurrent.{ExecutionContext, Future}

/** Controller untuk mengelola operasi CRUD pada entitas User.
  *
  * @param cc
  *   Komponen Controller.
  * @param userRepository
  *   Repository untuk interaksi database User.
  * @param ec
  *   ExecutionContext untuk operasi asinkron.
  */
@Singleton
class UserController @Inject() (
    cc: ControllerComponents,
    userRepository: UserRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  // Menambahkan user baru. Endpoint: POST /users
  def createUser: Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.addData(request = request, repository = userRepository, entityName = "User")
  }

  // Memperbarui user yang sudah ada. Endpoint: PUT /users/:id
  def updateUser(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.updateData(id = id, request = request, repository = userRepository, entityName = "User")
  }

  // Menghapus user secara permanen berdasarkan ID. Endpoint: DELETE /users/:id
  def deleteUser(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.deleteData(id = id, repository = userRepository, entityName = "User")
  }

  // Menghapus user temp berdasarkan ID. Endpoint: DELETE /users/:id
  def softDeleteUser(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.softDeleteData(id = id, repository = userRepository, entityName = "User")
  }

  // Mendapatkan semua user. Endpoint: GET /users
  def getAllUsers: Action[AnyContent] = Action.async {
    ControllerHelper.findAllData(repository = userRepository, entityName = "User")
  }

  // Mendapatkan user berdasarkan ID. Endpoint: GET /users/:id
  def getUserById(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findByIdData(id = id, repository = userRepository, entityName = "User")
  }

}
