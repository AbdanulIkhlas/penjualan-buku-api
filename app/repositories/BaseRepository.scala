package repositories

import scala.concurrent.Future

trait BaseRepository[T] {
  def create(entity: T): Future[T]
  def update(id: Long, entity: T): Future[Int]
  def delete(id: Long): Future[Int]
  def softDelete(id: Long): Future[Int]
  def findAll(): Future[Seq[T]]
  def findById(id: Long): Future[Option[T]]
}
