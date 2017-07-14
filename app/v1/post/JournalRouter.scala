package v1.post

import javax.inject.Inject

import play.api.routing.Router.Routes
import play.api.routing.SimpleRouter
import play.api.routing.sird._

/**
 * Created by admin on 6/15/17.
 */
class JournalRouter @Inject()(controller: JournalController) extends SimpleRouter {
  override def routes: Routes = {
    case GET(p"/") => controller.index

    case POST(p"/") => controller.process

    case GET(p"/$id") => controller.show(id)
  }
}
