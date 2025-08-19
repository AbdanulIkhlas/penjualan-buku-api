package utils

import play.api.libs.json.{JsValue, Json, Writes}
import play.api.mvc.{Result, Results}
import play.api.http.Status._


object ResponseHelper extends Results {

  /** respons sukses dengan status OK (200).
    *
    * @param data
    *   Data yang akan disertakan dalam respons.
    * @param message
    *   Pesan sukses
    * @tparam T
    *   Tipe data yang akan di-serialize ke JSON.
    * @return
    *   Objek Result
    */
  def success[T](data: T, message: String)(implicit writes: Writes[T]): Result = {
    val jsonResponse = Json.obj(
      "status"  -> "success",
      "code"    -> OK,
      "message" -> message,
      "data"    -> Json.toJson(data)
    )
    Ok(jsonResponse)
  }

  /** respons sukses tanpa data, untuk operasi DELETE.
    *
    * @param message
    *   Pesan sukses
    * @return
    *   Objek Result
    */
  def successNoContent(message: String): Result = {
    val jsonResponse = Json.obj(
      "status"  -> "success",
      "code"    -> OK,
      "message" -> message
    )
    Ok(jsonResponse)
  }

  /** respons dengan status Created (201).
    *
    * @param data
    *   Data yang akan disertakan dalam respons.
    * @param message
    *   Pesan sukses .
    * @tparam T
    *   Tipe data yang akan di-serialize ke JSON.
    * @return
    *   Objek Result
    */
  def created[T](data: T, message: String )(implicit writes: Writes[T]): Result = {
    val jsonResponse = Json.obj(
      "status"  -> "success",
      "code"    -> CREATED,
      "message" -> message,
      "data"    -> Json.toJson(data)
    )
    Created(jsonResponse)
  }

  /** respons error dengan status Bad Request (400).
    *
    * @param message
    *   Pesan error.
    * @return
    *   Objek Result
    */
  def badRequest(message: String): Result = {
    val jsonResponse = Json.obj(
      "status"  -> "error",
      "code"    -> BAD_REQUEST,
      "message" -> message
    )
    BadRequest(jsonResponse)
  }

  /** respons error dengan status Not Found (404).
    *
    * @param message
    *   Pesan error.
    * @return
    *   Objek Result
    */
  def notFound(message: String): Result = {
    val jsonResponse = Json.obj(
      "status"  -> "error",
      "code"    -> NOT_FOUND,
      "message" -> message
    )
    NotFound(jsonResponse)
  }

  /** respons error dengan status Internal Server Error (500).
    *
    * @param message
    *   Pesan error.
    * @return
    *   Objek Result
    */
  def internalServerError(message: String): Result = {
    val jsonResponse = Json.obj(
      "status"  -> "error",
      "code"    -> INTERNAL_SERVER_ERROR,
      "message" -> message
    )
    InternalServerError(jsonResponse)
  }

//  /** Membuat respons error dengan status Conflict (409), untuk duplikasi data.
//    *
//    * @param message
//    *   Pesan error.
//    * @return
//    *   Objek Result
//    */
//  def conflict(message: String): Result = {
//    val jsonResponse = Json.obj(
//      "status"  -> "error",
//      "code"    -> CONFLICT,
//      "message" -> message
//    )
//    Conflict(jsonResponse)
//  }
}
