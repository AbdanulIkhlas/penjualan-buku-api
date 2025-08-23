package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import repositories.GenreRepository
import utils.ControllerHelper

import scala.concurrent.ExecutionContext

/** Controller untuk mengelola operasi CRUD pada entitas Genre.
  *
  * @param cc
  *   Komponen Controller.
  * @param genreRepository
  *   Repository untuk interaksi database Genre.
  * @param ec
  *   ExecutionContext untuk operasi asinkron.
  */
@Singleton
class GenreController @Inject() (
    cc: ControllerComponents,
    genreRepository: GenreRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

//  def createGenre: Action[JsValue] = Action.async(parse.json) { request =>
////    println("Isi request body: " + request.body)
//    request.body
//      .validate[Genre]
//      .fold(
//        errors => {
//          Future.successful(ResponseHelper.badRequest(s"Data genre tidak valid: ${JsError.toJson(errors)}"))
//        },
//        genre => {
////          println("genre: " + genre)
////          println("genre repository: " + genreRepository)
////          println("genre repository create: " + genreRepository.create(genre))
//          genreRepository
//            .create(genre)
//            .map { createdGenre =>
//              println("createdGenre: " + createdGenre)
//              ResponseHelper.created(createdGenre, "Genre berhasil dibuat.")
//            }
//            .recover { case e: Exception =>
//              ResponseHelper.internalServerError(s"Gagal membuat genre: ${e.getMessage}")
//            }
//        }
//      )
//  }
  // Menambahkan genre baru. Endpoint: POST /genres
  def createGenre: Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.addData(request = request, repository = genreRepository, entityName = "User")
  }

//  def updateGenre(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
//    request.body
//      .validate[Genre]
//      .fold(
//        errors => {
//          Future.successful(ResponseHelper.badRequest(s"Data genre tidak valid: ${JsError.toJson(errors)}"))
//        },
//        genre => {
//          genreRepository
//            .update(id, genre)
//            .map { rowsAffected =>
//              if (rowsAffected > 0) {
//                ResponseHelper.successNoContent(s"Genre dengan ID $id berhasil diperbarui.")
//              } else {
//                ResponseHelper.notFound(s"Genre dengan ID $id tidak ditemukan.")
//              }
//            }
//            .recover { case e: Exception =>
//              ResponseHelper.internalServerError(s"Gagal memperbarui genre: ${e.getMessage}")
//            }
//        }
//      )
//  }
  // Memperbarui genre yang sudah ada. Endpoint: PUT /genres/:id
  def updateGenre(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    ControllerHelper.updateData(id = id, request = request, repository = genreRepository, entityName = "User")
  }

  // Menghapus genre berdasarkan ID. Endpoint: DELETE /genres/permanent/:id
  def deleteGenre(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.deleteData(id = id, repository = genreRepository, entityName = "User")
  }

//  def softDeleteGenre(id: Long): Action[AnyContent] = Action.async {
//    genreRepository
//      .softDelete(id)
//      .map { rowsAffected =>
//        if (rowsAffected > 0) {
//          ResponseHelper.successNoContent(s"Genre dengan ID $id berhasil dihapus.")
//        } else {
//          ResponseHelper.notFound(s"Genre dengan ID $id tidak ditemukan.")
//        }
//      }
//      .recover { case e: Exception =>
//        ResponseHelper.internalServerError(s"Gagal menghapus genre: ${e.getMessage}")
//      }
//  }
  // Menghapus genre berdasarkan ID. Endpoint: DELETE /genres/:id
  def softDeleteGenre(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.softDeleteData(id = id, repository = genreRepository, entityName = "User")
  }

//  def getAllGenres: Action[AnyContent] = Action.async {
//    genreRepository
//      .findAll()
//      .map { genres =>
//        ResponseHelper.success(genres, "Daftar genre berhasil diambil.")
//      }
//      .recover { case e: Exception =>
//        ResponseHelper.internalServerError(s"Gagal mengambil daftar genre: ${e.getMessage}")
//      }
//  }
  // Mendapatkan semua genre. Endpoint: GET /genres
  def getAllGenres: Action[AnyContent] = Action.async {
    ControllerHelper.findAllData(repository = genreRepository, entityName = "User")
  }

//  def getGenreById(id: Long): Action[AnyContent] = Action.async {
//    genreRepository
//      .findById(id)
//      .map {
//        case Some(genre) =>
//          ResponseHelper.success(genre, s"Genre dengan ID $id berhasil ditemukan.")
//        case None =>
//          ResponseHelper.notFound(s"Genre dengan ID $id tidak ditemukan.")
//      }
//      .recover { case e: Exception =>
//        ResponseHelper.internalServerError(s"Gagal mengambil genre: ${e.getMessage}")
//      }
//  }
  // Mendapatkan genre berdasarkan ID. Endpoint: GET /genres/:id
  def getGenreById(id: Long): Action[AnyContent] = Action.async {
    ControllerHelper.findByIdData(id = id, repository = genreRepository, entityName = "User")
  }
}
