package viewController

import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class ProductController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  // Halaman produk
  def products(id: Long, name: String) = Action { implicit request: Request[AnyContent] =>
    // Di sini bisa ambil data buku atau data lain sesuai kebutuhan
    Ok(views.html.pages.product(id, name))
  }
}
