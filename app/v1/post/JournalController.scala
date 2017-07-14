package v1.post

import javax.inject.Inject

import play.api.data.Form
import play.api.libs.json.Json
import play.api.mvc.AnyContent
import play.api.mvc._
import play.mvc.Controller

import scala.concurrent.{Future, ExecutionContext}

/**
 * Created by admin on 6/15/17.
 */

case class JournalFormInput(id: Int, name: String, postIds: List[Int])

class JournalController @Inject()(
                                   action: PostAction,
                                   handler: JournalResourceHandler)
                                 (implicit ec: ExecutionContext) extends Controller {

  private val form: Form[JournalFormInput] = {
    import play.api.data.Forms._

    Form(
      mapping(
        "id" -> number,
        "name" -> text,
        "postIds" -> list(number)
      )(JournalFormInput.apply)(JournalFormInput.unapply)
    )
  }

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)

  def index: Action[AnyContent] = {
    action.async { implicit request =>
      handler.find.map {
        journals => Results.Ok(Json.toJson(journals))
      }
    }
  }

  def process = Action(BodyParsers.parse.json[JournalResource]) {
        logger.trace("Inside process")
    request =>
      val journal = request.body
      handler.create(journal)
      Results.Ok(Json.obj("status" -> "OK", "message" -> ("journal saved w/ ID " +journal.id)))
  }

  def show(id: String): Action[AnyContent] = {
    action.async { implicit request =>
      handler.lookup(id.toInt).map { post =>
        Results.Ok(Json.toJson(post))
      }
    }
  }
}
