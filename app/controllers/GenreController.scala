package controllers

import javax.inject._
import play.api.mvc._
import play.api.libs.json._
import repositories.GenreRepository
import models.Genre
import utils.ResponseHelper
import scala.concurrent.{ExecutionContext, Future}

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

  /** Membuat genre baru. Endpoint: POST /genres
    */
  def createGenre: Action[JsValue] = Action.async(parse.json) { request =>
//    println("Isi request body: " + request.body)
    request.body
      .validate[Genre]
      .fold(
        errors => {
          Future.successful(ResponseHelper.badRequest(s"Data genre tidak valid: ${JsError.toJson(errors)}"))
        },
        genre => {
//          println("genre: " + genre)
//          println("genre repository: " + genreRepository)
//          println("genre repository create: " + genreRepository.create(genre))
          genreRepository
            .create(genre)
            .map { createdGenre =>
              println("createdGenre: " + createdGenre)
              ResponseHelper.created(createdGenre, "Genre berhasil dibuat.")
            }
            .recover { case e: Exception =>
              ResponseHelper.internalServerError(s"Gagal membuat genre: ${e.getMessage}")
            }
        }
      )
  }

  /** Memperbarui genre yang sudah ada. Endpoint: PUT /genres/:id
    *
    * @param id
    *   ID genre yang akan diperbarui.
    */
  def updateGenre(id: Long): Action[JsValue] = Action.async(parse.json) { request =>
    request.body
      .validate[Genre]
      .fold(
        errors => {
          Future.successful(ResponseHelper.badRequest(s"Data genre tidak valid: ${JsError.toJson(errors)}"))
        },
        genre => {
          genreRepository
            .update(id, genre)
            .map { rowsAffected =>
              if (rowsAffected > 0) {
                ResponseHelper.successNoContent(s"Genre dengan ID $id berhasil diperbarui.")
              } else {
                ResponseHelper.notFound(s"Genre dengan ID $id tidak ditemukan.")
              }
            }
            .recover { case e: Exception =>
              ResponseHelper.internalServerError(s"Gagal memperbarui genre: ${e.getMessage}")
            }
        }
      )
  }

  /** Menghapus genre berdasarkan ID. Endpoint: DELETE /genres/:id
    *
    * @param id
    *   ID genre yang akan dihapus.
    */
  def deleteGenre(id: Long): Action[AnyContent] = Action.async {
    genreRepository
      .delete(id)
      .map { rowsAffected =>
        if (rowsAffected > 0) {
          ResponseHelper.successNoContent(s"Genre dengan ID $id berhasil dihapus permanen.")
        } else {
          ResponseHelper.notFound(s"Genre dengan ID $id tidak ditemukan.")
        }
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal menghapus genre: ${e.getMessage}")
      }
  }

  def softDeleteGenre(id: Long): Action[AnyContent] = Action.async {
    genreRepository
      .softDelete(id)
      .map { rowsAffected =>
        if (rowsAffected > 0) {
          ResponseHelper.successNoContent(s"Genre dengan ID $id berhasil dihapus.")
        } else {
          ResponseHelper.notFound(s"Genre dengan ID $id tidak ditemukan.")
        }
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal menghapus genre: ${e.getMessage}")
      }
  }

  /** Mendapatkan semua genre. Endpoint: GET /genres
    */
  def getAllGenres: Action[AnyContent] = Action.async {
    genreRepository
      .findAll()
      .map { genres =>
        ResponseHelper.success(genres, "Daftar genre berhasil diambil.")
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal mengambil daftar genre: ${e.getMessage}")
      }
  }

  /** Mendapatkan genre berdasarkan ID. Endpoint: GET /genres/:id
    *
    * @param id
    *   ID genre.
    */
  def getGenreById(id: Long): Action[AnyContent] = Action.async {
    genreRepository
      .findById(id)
      .map {
        case Some(genre) =>
          ResponseHelper.success(genre, s"Genre dengan ID $id berhasil ditemukan.")
        case None =>
          ResponseHelper.notFound(s"Genre dengan ID $id tidak ditemukan.")
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal mengambil genre: ${e.getMessage}")
      }
  }
}
