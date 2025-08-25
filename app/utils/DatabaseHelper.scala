package utils

import play.api.db.Database
import anorm._
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import java.sql.PreparedStatement

/** Manajemen koneksi dan transaksi database.
  * @param db
  *   Instans Play Database.
  * @param ec
  *   ExecutionContext untuk operasi asinkron.
  */
class DatabaseHelper @Inject() (db: Database)(implicit ec: ExecutionContext) {

  /** Menjalankan operasi database dalam sebuah koneksi.
    *
    * @param block
    *   Blok kode yang akan dijalankan dengan koneksi database.
    * @tparam T
    *   Tipe hasil dari blok kode.
    * @return
    *   Future yang berisi hasil dari blok kode.
    */
  private def withConnection[T](block: java.sql.Connection => T): Future[T] = Future {
    db.withConnection(block)
  }

  /** Menjalankan operasi database dalam sebuah transaksi. Jika ada kesalahan, transaksi akan di-rollback.
    *
    * @param block
    *   Blok kode yang akan dijalankan dengan koneksi database dalam transaksi.
    * @tparam T
    *   Tipe hasil dari blok kode.
    * @return
    *   Future yang berisi hasil dari blok kode.
    */
  private def withTransaction[T](block: java.sql.Connection => T): Future[T] = Future {
    db.withTransaction(block)
  }

//  private def filterDataType(key: String): String =
//    if (key.indexOf(".") == -1) key
//    else key.substring(0, key.indexOf("."))

  /** Tambah data
    *
    * @param tableName
    *   Nama tabel.
    * @param data
    *   Data yang akan disimpan dalam tabel.
    * @return
    *   Future yang berisi ID yang telah disimpan.
    */
  def insertAndReturnId(
      tableName: String,
      data: Map[String, Any]
  ): Future[Long] = withTransaction { implicit connection =>
    val columnNames: String     = data.keys.mkString(", ")
    val keyValueColumns: String = data.keys.map(key => s"{$key}").mkString(", ")

    // Mapping kolom menggunakan NamedParameter (bawaan anorm) `perlu implicit anyToStatement`
    val mappingValueColumn: Seq[NamedParameter] =
      data.map { case (columnName, columnValue) =>
        NamedParameter(columnName, columnValue)
      }.toSeq
    println("[DEBUG] Data: " + data)
    println("[DEBUG] Column names: " + columnNames)
    println("[DEBUG] Key value columns: " + keyValueColumns)
    println("[DEBUG] Mapping value column: " + mappingValueColumn)

    val sqlQuery = SQL(s"INSERT INTO $tableName ($columnNames) VALUES ($keyValueColumns)").on(mappingValueColumn: _*)

    val insertedId: Long = sqlQuery.executeInsert(SqlParser.scalar[Long].single)

    // returnnya
    insertedId
  }

  /** Update data
    *
    * @param tableName
    *   Nama tabel.
    * @param data
    *   Kolom yang akan diupdate beserta nilainya.
    * @param idColumn
    *   Nama kolom primary key / identifier.
    * @param idValue
    *   Nilai primary key untuk WHERE.
    * @return
    *   Future yang berisi jumlah baris yang terpengaruh (1 jika berhasil, 0 jika tidak ditemukan).
    */
  def updateRowById(
      tableName: String,
      data: Map[String, Any],
      idColumn: String,
      idValue: Any,
      softDeleteColumnName: String
  ): Future[Int] = withTransaction { implicit connection =>
    val setColumn: String = data.keys.map(col => s"$col = {$col}").mkString(", ")

    // Mapping kolom SET menjadi NamedParameter → {columnName -> columnValue}
    val mappingValueColumn: Seq[NamedParameter] =
      data.map { case (columnName, columnValue) => NamedParameter(columnName, columnValue) }.toSeq

    val mappingWhere: NamedParameter       = NamedParameter(s"where_$idColumn", idValue)
    val allParameters: Seq[NamedParameter] = mappingValueColumn :+ mappingWhere
    val sqlQuery = SQL(
      s"UPDATE $tableName SET $setColumn WHERE $idColumn = {where_$idColumn} AND $softDeleteColumnName = FALSE"
    ).on(allParameters: _*)

    // Eksekusi update → return jumlah row yang terupdate
    sqlQuery.executeUpdate()
  }

  /** Soft delete data
    *
    * @param tableName
    *   Nama tabel.
    * @param idColumn
    *   Nama kolom primary key / identifier.
    * @param idValue
    *   Nilai primary key untuk WHERE.
    * @param softDeleteColumnName
    *   Nama kolom soft delete.
    * @return
    *   Future yang berisi jumlah baris yang terpengaruh (1 jika berhasil, 0 jika tidak ditemukan).
    */
  def softDeleteRowById(
      tableName: String,           // nama tabel
      idColumn: String,            // nama kolom id
      idValue: Any,                // nilai id
      softDeleteColumnName: String // nama kolom soft delete
  ): Future[Int] = withTransaction { implicit connection =>
    // NOTE : // kan where $idColumn = {idValue}, nah di on nya jg harus sama, karena {idValue} maka on nya "idValue" -> idValue(nilai dari id di parameter)
    val sql =
      SQL(
        s"UPDATE $tableName SET $softDeleteColumnName = TRUE WHERE $idColumn = {idValue} AND $softDeleteColumnName = FALSE"
      ).on("idValue" -> idValue)
    sql.executeUpdate()
  }

  /** Hapus genre permanen berdasarkan ID.
    *
    * @param tableName
    *   Nama tabel.
    * @param idColumn
    *   Nama kolom primary key / identifier.
    * @param idValue
    *   Nilai primary key untuk WHERE.
    * @return
    *   Future yang berisi jumlah baris yang terpengaruh (1 jika berhasil, 0 jika tidak ditemukan).
    */
  def deletePermanentRowById(
      tableName: String,
      idColumn: String,
      idValue: Any
  ): Future[Int] = withConnection { implicit connection =>
    val sql = SQL(s"DELETE FROM $tableName WHERE $idColumn = {idValue}").on("idValue" -> idValue)
    sql.executeUpdate()
  }

  /** Helper untuk SELECT all
    *
    * @param table
    *   Nama tabel.
    * @param parser
    *   Parser Anorm untuk mengonversi row ResultSet menjadi objek.
    * @param condition
    *   Kondisi untuk WHERE (opsional).
    * @tparam T
    *   Tipe objek yang akan dikembalikan.
    * @return
    *   Future yang berisi daftar objek.
    */
  def findAll[T](table: String, parser: RowParser[T], condition: Option[String] = None): Future[Seq[T]] = {
    val customConditionWhere = condition.map(c => s"WHERE $c").getOrElse("")
    val query                = SQL(s"SELECT * FROM $table $customConditionWhere")
    println(s"[DEBUG] SQL Query findAll: $query")
    withConnection { implicit conn =>
      query.as(parser.*)
    }
  }

  /** Helper untuk SELECT by ID
    *
    * @param tableName
    *   Nama tabel.
    * @param idColumn
    *   Nama kolom primary key / identifier.
    * @param idValue
    *   Nilai primary key untuk WHERE.
    * @param parser
    *   Parser Anorm untuk mengonversi row ResultSet menjadi objek.
    * @tparam T
    *   Tipe objek yang akan dikembalikan.
    * @return
    *   Future yang berisi Option[T], None jika tidak ditemukan.
    */
  def findByIdRow[T](
      tableName: String,
      idColumn: String,
      idValue: Any,
      parser: RowParser[T]
  ): Future[Option[T]] = withConnection { implicit connection =>
    println(s"[DEBUG] SQL Query findByIdRow: nama table : $tableName, nama kolom : $idColumn, value colom : $idValue")
    val sql = SQL(s"SELECT * FROM $tableName WHERE $idColumn = {$idColumn}").on(idColumn -> idValue)

    sql.as(parser.singleOpt)
  }

  // Implicit untuk handle Any di NamedParameter value
  implicit val anyToStatement: ToStatement[Any] = new ToStatement[Any] {
    def set(s: PreparedStatement, index: Int, a: Any): Unit = a match {
      case v: String                  => s.setString(index, v)
      case v: Int                     => s.setInt(index, v)
      case v: Long                    => s.setLong(index, v)
      case v: Double                  => s.setDouble(index, v)
      case v: scala.math.BigDecimal   => s.setBigDecimal(index, v.bigDecimal)
      case v: java.math.BigDecimal    => s.setBigDecimal(index, v)
      case v: Boolean                 => s.setBoolean(index, v)
      case v: java.util.Date          => s.setTimestamp(index, new java.sql.Timestamp(v.getTime))
      case v: java.time.LocalDate     => s.setDate(index, java.sql.Date.valueOf(v))
      case v: java.time.LocalDateTime => s.setTimestamp(index, java.sql.Timestamp.valueOf(v))
      case null                       => s.setObject(index, null)
      case Some(x)                    => set(s, index, x) // recursive unwrap Option
      case None                       => s.setObject(index, null)
      case other                      => s.setObject(index, other.toString)
    }
  }

}
