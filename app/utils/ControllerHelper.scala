package utils

import play.api.mvc._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import repositories.BaseRepository

object ControllerHelper {

  /** General function untuk nambah data ke tabel
    *
    * @param request
    *   JSON request
    * @param repository
    *   repository tabel
    * @param entityName
    *   nama tabel untuk pesan success/error (contoh: "User", "Genre")
    * @param ec
    *   ExecutionContext karena menggunakan future (async)
    * @param reads
    *   Reads[T] untuk mengonversi JSON ke case class
    * @param writes
    *   Writes[T] untuk mengonversi case class ke JSON
    * @tparam T
    *   case class tabel (contoh : User, Genre)
    * @return
    *   Future[Result]
    */
  def addData[T](
      request: Request[JsValue],
      repository: BaseRepository[T],
      entityName: String
  )(implicit reads: Reads[T], writes: Writes[T], ec: ExecutionContext): Future[Result] = {
    println("[MARK] Masuk function addData controller helper")
    println("[DEBUG] request body: " + request.body)
    request.body
      .validate[T]
      .fold(
        errors => {
          Future.successful(
            ResponseHelper.badRequest(s"Data $entityName tidak valid: ${JsError.toJson(errors)}")
          )
        },
        entity => {
          repository
            .create(entity) // dalam bentuk Future[T] : dimana T = Case class
            .map { createdEntity =>
              ResponseHelper.created(createdEntity, s"$entityName berhasil dibuat.")
            }
            .recover { case e: Exception =>
              ResponseHelper.internalServerError(s"Gagal membuat $entityName: ${e.getMessage}")
            }
        }
      )
  }

  /** General Function untuk update data di tabel
    *
    * @param id
    *   ID dari data yang akan diupdate
    * @param request
    *   JSON request
    * @param repository
    *   repository tabel
    * @param entityName
    *   nama tabel untuk pesan success/error (contoh: "User", "Genre", dsb)
    * @param ec
    *   ExecutionContext karena menggunakan future (async)
    * @param reads
    *   Reads[T] untuk mengonversi JSON ke case class
    * @param writes
    *   Writes[T] untuk mengonversi case class ke JSON
    * @tparam T
    *   case class tabel (contoh : User, Genre, dsb)
    * @return
    *   Future[Result]
    */
  def updateData[T](
      id: Long,
      request: Request[JsValue],
      repository: BaseRepository[T],
      entityName: String
  )(implicit reads: Reads[T], writes: Writes[T], ec: ExecutionContext): Future[Result] = {
    println("[MARK] Masuk function updateData controller helper")
    println(s"[DEBUG] request body update $entityName: " + request.body)
    println(s"[DEBUG] dipanggil dari: $entityName")

    request.body
      .validate[T]
      .fold(
        errors => {
          Future.successful(
            ResponseHelper.badRequest(s"Data $entityName tidak valid: ${JsError.toJson(errors)}")
          )
        },
        entity => {
          repository
            .update(id, entity) // return Future[Int]
            .map { rowsAffected =>
              if (rowsAffected > 0) {
                ResponseHelper.successNoContent(s"$entityName dengan ID $id berhasil diperbarui.")
              } else {
                ResponseHelper.notFound(s"$entityName dengan ID $id tidak ditemukan.")
              }
            }
            .recover { case e: Exception =>
              ResponseHelper.internalServerError(s"Gagal memperbarui $entityName: ${e.getMessage}")
            }
        }
      )
  }

  /** General function untuk delete permanen data di tabel
    *
    * @param id
    *   id data yang akan dihapus permanen
    * @param repository
    *   repository tabel
    * @param entityName
    *   nama tabel untuk pesan success/error (contoh: "User", "Genre", dsb)
    * @param ec
    *   ExecutionContext karena menggunakan future (async)
    * @tparam T
    *   case class tabel (contoh : User, Genre, dsb)
    * @return
    *   Future[Result]
    */
  def deleteData[T](
      id: Long,
      repository: BaseRepository[T],
      entityName: String
  )(implicit ec: ExecutionContext): Future[Result] = {
    println("[MARK] Masuk function deleteData controller helper")
    println(s"[DEBUG] delete request for $entityName with id=$id")

    repository
      .delete(id) // return Future[Int]
      .map { rowsAffected =>
        if (rowsAffected > 0) {
          ResponseHelper.successNoContent(s"$entityName dengan ID $id berhasil dihapus permanen.")
        } else {
          ResponseHelper.notFound(s"$entityName dengan ID $id tidak ditemukan.")
        }
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal menghapus $entityName: ${e.getMessage}")
      }
  }

  /** General function untuk softdelete data di tabel
    *
    * @param id
    *   id data yang akan dihapus temp
    * @param repository
    *   repository tabel
    * @param entityName
    *   nama tabel
    * @param ec
    *   ExecutionContext karena menggunakan future (async)
    * @tparam T
    *   case class tabel (contoh : User, Genre, dsb)
    * @return
    */
  def softDeleteData[T](
      id: Long,
      repository: BaseRepository[T],
      entityName: String
  )(implicit ec: ExecutionContext): Future[Result] = {
    println("[MARK] Masuk function softDeleteData controller helper")
    println(s"[DEBUG] soft delete request for $entityName with id=$id")

    repository
      .softDelete(id) // return Future[Int]
      .map { rowsAffected =>
        if (rowsAffected > 0) {
          ResponseHelper.successNoContent(s"$entityName dengan ID $id berhasil dihapus (soft delete).")
        } else {
          ResponseHelper.notFound(s"$entityName dengan ID $id tidak ditemukan.")
        }
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal menghapus (soft delete) $entityName: ${e.getMessage}")
      }
  }

  /** General function untuk mengambil semua data di tabel
    *
    * @param repository
    *   repository tabel
    * @param entityName
    *   nama tabel
    * @param writes
    *   Writes[T] untuk mengonversi case class ke JSON
    * @param ec
    *   ExecutionContext karena menggunakan future (async)
    * @tparam T
    *   case class tabel
    * @return
    *   Future[Result]
    */
  def findAllData[T](
      repository: BaseRepository[T],
      entityName: String
  )(implicit writes: Writes[T], ec: ExecutionContext): Future[Result] = {
    println("[MARK] Masuk function findAllData controller helper")
    println(s"[DEBUG] findAll request for $entityName")

    repository
      .findAll() // return Future[Seq[T]]
      .map { entities =>
        ResponseHelper.success(entities, s"Daftar $entityName berhasil diambil.")
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal mengambil daftar $entityName: ${e.getMessage}")
      }
  }

  /** General function untuk mengambil data berdasarkan ID
    *
    * @param id
    *   ID data yang akan diambil
    * @param repository
    *   repository tabel
    * @param entityName
    *   nama tabel
    * @param writes
    *   Writes[T] untuk mengonversi case class ke JSON
    * @param ec
    *   ExecutionContext karena menggunakan future (async)
    * @tparam T
    *   case class tabel
    * @return
    *   Future[Result]
    */
  def findByIdData[T](
      id: Long,
      repository: BaseRepository[T],
      entityName: String
  )(implicit writes: Writes[T], ec: ExecutionContext): Future[Result] = {
    println("[MARK] Masuk function findByIdData controller helper")
    println(s"[DEBUG] findById request for $entityName with id: $id")

    repository
      .findById(id) // return Future[Option[T]]
      .map {
        case Some(entity) =>
          ResponseHelper.success(entity, s"$entityName dengan ID $id berhasil ditemukan.")
        case None =>
          ResponseHelper.notFound(s"$entityName dengan ID $id tidak ditemukan.")
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal mengambil $entityName: ${e.getMessage}")
      }
  }

  def findByIdUser[T](
      idUser: Long,
      repository: BaseRepository[T],
      entityName: String
  )(implicit writes: Writes[T], ec: ExecutionContext): Future[Result] = {
    println("[MARK] Masuk function findByIdUser controller helper")
    println(s"[DEBUG] findByIdUser request for $entityName with id User: $idUser")

    repository
      .findByIdUser(idUser) // return Future[Seq[T]]
      .map { entities =>
        ResponseHelper.success(entities, s"Daftar $entityName dengan ID User $idUser berhasil diambil.")
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal mengambil daftar $entityName: ${e.getMessage}")
      }
  }

  def findCartBookByIdCart[T](
      idCart: Long,
      repository: BaseRepository[T],
      entityName: String
  )(implicit writes: Writes[T], ec: ExecutionContext): Future[Result] = {
    println("[MARK] Masuk function findCartBookByIdCart controller helper")
    println(s"[DEBUG] findCartBookByIdCart request for $entityName with idCart: $idCart")

    repository
      .findCartBookByIdCart(idCart) // return Future[Seq[T]]
      .map { entities =>
        ResponseHelper.success(entities, s"Daftar $entityName dengan ID Keranjang $idCart berhasil diambil.")
      }
      .recover { case e: Exception =>
        ResponseHelper.internalServerError(s"Gagal mengambil daftar $entityName: ${e.getMessage}")
      }
  }
}
