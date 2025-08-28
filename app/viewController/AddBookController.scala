package viewController

import javax.inject._
import play.api.mvc._
import scala.concurrent.ExecutionContext
import repositories.GenreRepository
import models.Genre
import views.html.pages.addBook

@Singleton
class AddBookController @Inject() (
    cc: ControllerComponents,
    genreRepository: GenreRepository
)(implicit ec: ExecutionContext)
    extends AbstractController(cc) {

  // Render halaman tambah buku.
  def showAddBookPage(userId: Long, userName: String): Action[AnyContent] = Action.async { implicit request =>
    genreRepository.findAll().map { genres: Seq[Genre] =>
      Ok(addBook(userId, userName, genres))
    }
  }

}
