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

//  def createUser: Action[JsValue] = Action.async(parse.json) { request =>
//    request.body
//      .validate[User]
//      .fold(
//        errors => {
//          Future.successful(ResponseHelper.badRequest(s"Data user tidak valid: ${JsError.toJson(errors)}"))
//        },
//        user => {
//          println("user: " + user)
//          userRepository
//            .create(user)
//            .map { createdUser =>
//              println("createdUser: " + createdUser)
//              ResponseHelper.created(createdUser, "User berhasil dibuat.")
//            }
//            .recover { case e: Exception =>
//              println("Masuk recover Error: " + e.getMessage)
//              ResponseHelper.internalServerError(s"Gagal membuat user: ${e.getMessage}")
//            }
//        }
//      )
//  }
  // Menambahkan user baru. Endpoint: POST /users
  def createUser: Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.addData(request = request, repository = userRepository, entityName = "User")
  }

//  def updateUser(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
//    request.body
//      .validate[User]
//      .fold(
//        errors => {
//          Future.successful(ResponseHelper.badRequest(s"Data user tidak valid: ${JsError.toJson(errors)}"))
//        },
//        user => {
//          userRepository
//            .update(id, user)
//            .map { rowsAffected =>
//              if (rowsAffected > 0) {
//                ResponseHelper.successNoContent(s"User dengan ID $id berhasil diperbarui.")
//              } else {
//                ResponseHelper.notFound(s"User dengan ID $id tidak ditemukan.")
//              }
//            }
//            .recover { case e: Exception =>
//              ResponseHelper.internalServerError(s"Gagal memperbarui user: ${e.getMessage}")
//            }
//        }
//      )
//  }
  // Memperbarui user yang sudah ada. Endpoint: PUT /users/:id
  def updateUser(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.updateData(id = id, request = request, repository = userRepository, entityName = "User")
  }

//  def deleteUser(id: Long): Action[AnyContent] = Action.async {
//    userRepository
//      .delete(id)
//      .map { rowsAffected =>
//        if (rowsAffected > 0) {
//          ResponseHelper.successNoContent(s"User dengan ID $id berhasil dihapus permanen.")
//        } else {
//          ResponseHelper.notFound(s"User dengan ID $id tidak ditemukan.")
//        }
//      }
//      .recover { case e: Exception =>
//        ResponseHelper.internalServerError(s"Gagal menghapus user: ${e.getMessage}")
//      }
//  }
  // Menghapus user secara permanen berdasarkan ID. Endpoint: DELETE /users/:id
  def deleteUser(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.deleteData(id = id, repository = userRepository, entityName = "User")
  }

//  def softDeleteUser(id: Long): Action[AnyContent] = Action.async {
//    userRepository
//      .softDelete(id)
//      .map { rowsAffected =>
//        if (rowsAffected > 0) {
//          ResponseHelper.successNoContent(s"User dengan ID $id berhasil dihapus.")
//        } else {
//          ResponseHelper.notFound(s"User dengan ID $id tidak ditemukan.")
//        }
//      }
//      .recover { case e: Exception =>
//        ResponseHelper.internalServerError(s"Gagal menghapus user: ${e.getMessage}")
//      }
//  }
  // Menghapus user temp berdasarkan ID. Endpoint: DELETE /users/:id
  def softDeleteUser(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.softDeleteData(id = id, repository = userRepository, entityName = "User")
  }

//  def getAllUsers: Action[AnyContent] = Action.async {
//    userRepository
//      .findAll()
//      .map { users =>
//        ResponseHelper.success(users, "Daftar user berhasil diambil.")
//      }
//      .recover { case e: Exception =>
//        ResponseHelper.internalServerError(s"Gagal mengambil daftar user: ${e.getMessage}")
//      }
//  }
  // Mendapatkan semua user. Endpoint: GET /users
  def getAllUsers: Action[AnyContent] = Action.async {
    ControllerHelper.findAllData(repository = userRepository, entityName = "User")
  }

//  def getUserById(id: Long): Action[AnyContent] = Action.async {
//    userRepository
//      .findById(id)
//      .map {
//        case Some(user) =>
//          ResponseHelper.success(user, s"User dengan ID $id berhasil ditemukan.")
//        case None =>
//          ResponseHelper.notFound(s"User dengan ID $id tidak ditemukan.")
//      }
//      .recover { case e: Exception =>
//        ResponseHelper.internalServerError(s"Gagal mengambil user: ${e.getMessage}")
//      }
//  }
  // Mendapatkan user berdasarkan ID. Endpoint: GET /users/:id
  def getUserById(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findByIdData(id = id, repository = userRepository, entityName = "User")
  }

}
