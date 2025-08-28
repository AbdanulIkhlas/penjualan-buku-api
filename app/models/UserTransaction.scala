package models

import anorm._
import anorm.SqlParser._
import play.api.libs.json.{Json, OFormat}
import java.time.LocalDateTime

// Detail buku dalam transaksi
case class UserTransactionBook(
    cartBookId: Long,
    quantity: Int,
    unitPrice: Double,
    totalPrice: Double,
    bookId: Long,
    title: String,
    author: String,
    price: Double
)

object UserTransactionBook {
  implicit val format: OFormat[UserTransactionBook] = Json.format[UserTransactionBook]

  val parser: RowParser[UserTransactionBook] = {
    get[Long]("cart_book_id") ~
      get[Int]("quantity") ~
      get[Double]("unit_price") ~
      get[Double]("book_total_price") ~
      get[Long]("book_id") ~
      get[String]("title") ~
      get[String]("author") ~
      get[Double]("book_price") map {
        case cartBookId ~ quantity ~ unitPrice ~ totalPrice ~ bookId ~ title ~ author ~ price =>
          UserTransactionBook(cartBookId, quantity, unitPrice, totalPrice, bookId, title, author, price)
      }
  }
}

// Transaksi beserta daftar buku
case class UserTransaction(
    transactionId: Long,
    cartId: Long,
    cartPrice: Double,
    deliveryFee: Double,
    totalPrice: Double,
    createdAt: Option[LocalDateTime],
    books: Seq[UserTransactionBook]
)

object UserTransaction {
  implicit val format: OFormat[UserTransaction] = Json.format[UserTransaction]

  val parser: RowParser[UserTransaction] = {
    get[Long]("transaction_id") ~
      get[Long]("cart_id") ~
      get[Double]("cart_price") ~
      get[Double]("delivery_service_price") ~
      get[Double]("total_price") ~
      get[Option[LocalDateTime]]("created_at") map { case transactionId ~ cartId ~ cartPrice ~ deliveryFee ~ totalPrice ~ createdAt =>
        UserTransaction(transactionId, cartId, cartPrice, deliveryFee, totalPrice, createdAt, Seq.empty)
      }
  }

  /** Parser gabungan transaksi + buku untuk repository
   * @return :
   *        Seq(
   *        (trxA, bookA1),
   *        (trxA, bookA2),
   *        (trxB, bookB1),
   *        ...
   *        )
   * */
  val transactionWithBookParser: RowParser[(UserTransaction, UserTransactionBook)] = {
    get[Long]("transaction_id") ~
      get[Long]("cart_id") ~
      get[Double]("cart_price") ~
      get[Double]("delivery_service_price") ~
      get[Double]("total_price") ~
      get[Option[LocalDateTime]]("created_at")~
      get[Long]("cart_book_id") ~
      get[Int]("quantity") ~
      get[Double]("unit_price") ~
      get[Double]("book_total_price") ~
      get[Long]("book_id") ~
      get[String]("title") ~
      get[String]("author") ~
      get[Double]("book_price") map {
        case transactionId ~ cartId ~ cartPrice ~ deliveryFee ~ totalPrice ~ createdAt ~
            cartBookId ~ quantity ~ unitPrice ~ bookTotal ~ bookId ~ title ~ author ~ price =>
          val transaction  = UserTransaction(transactionId, cartId, cartPrice, deliveryFee, totalPrice, createdAt, Seq.empty)
          val book = UserTransactionBook(cartBookId, quantity, unitPrice, bookTotal, bookId, title, author, price)
          (transaction, book)
      }
  }
}
