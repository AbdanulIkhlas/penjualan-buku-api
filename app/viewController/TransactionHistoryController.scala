package viewController

import play.api.mvc._

import javax.inject._
import scala.concurrent.ExecutionContext

@Singleton
class TransactionHistoryController @Inject()(cc: ControllerComponents)(implicit ec: ExecutionContext)
  extends AbstractController(cc) {

  // Halaman History Transaksi
  def historyTransaction(iduser: Long, name: String) = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.pages.transactionHistory(iduser, name))
  }
}
