package v1.post

import javax.inject.{Provider, Inject}

import com.google.inject.Guice
import play.api.libs.json._
import v1.MyPrivateModule

import scala.collection.mutable.ListBuffer
import scala.concurrent.{ExecutionContext, Future}


/**
 * Created by admin on 6/15/17.
 */

case class JournalResource(name: String, id: Int, posts: List[PostIdWithLink])

//case class JournalResource(name: String, id: Int, posts: List[Int])

case class PostIdWithLink(id: Int, link: String)


object JournalResource {
//  implicit val implicitWrites = new Writes[JournalResource] {
//
//    def writes(journal: JournalResource): JsValue = {
//      Json.obj(
//        "id" -> journal.id,
//        "name" -> journal.name,
//        "posts" -> journal.posts
//      )
//    }
//  }

  implicit val postIdWithLinkFormat = Json.format[PostIdWithLink]
  implicit val postIdWithLinkWrites = Json.writes[PostIdWithLink]
  implicit val postIdWithLinkReads = Json.reads[PostIdWithLink]
  //Automatic mapping from case class to JSON
  implicit val journalFormat = Json.format[JournalResource]
  implicit val journalWrites = Json.writes[JournalResource]

//  implicit val journalWrites = new Writes[JournalResource] {
//    def writes(journal: JournalResource): JsValue = {
//      import v1.MyModule
//      val injector = Guice.createInjector(new MyModule())
//
//      import net.codingwell.scalaguice.InjectorExtensions._
//      val routerProvider = injector.instance[Provider[PostRouter]]
//      val postIdWithLinkList = ListBuffer.empty[PostIdWithLink]
//      journal.posts.map(
//        postId => postIdWithLinkList += PostIdWithLink(id = postId, link = routerProvider.get.link(PostId(postId.toString)))
//      )
//      Json.obj(
//        "id" -> journal.id,
//        "name" -> journal.name,
//        "posts" -> postIdWithLinkList
//      )
//    }
//  }

  //Automatic JSON mapping to case class
  implicit val journalReads = Json.reads[JournalResource]



  //TODO: return the PostResource JSON for all of IDs in list
  //"posts2" -> new JournalResourceHandler().getPosts(journal.list)

}

class JournalResourceHandler @Inject()(
                                        journalRepository: JournalRepositoryImpl,
                                        postResourceHandler: PostResourceHandler,
                                        routerProvider: Provider[PostRouterImpl]
                                        )(implicit ec: ExecutionContext) {

//  def getPosts(ids: List[Int]): JsValue = {
//    Json.toJson(ids.map {
//      id => postResourceHandler.lookup(id.toString)
//    })
//  }

  def create(journalInput: JournalFormInput): Future[JournalResource] = {
    val data = JournalData(JournalId(journalInput.id.toString), journalInput.name, journalInput.postIds)
    journalRepository.create(data).map {
      id => createJournalResource(data)
    }
  }

//  def create(journalInput: JournalResource): Future[JournalResource] = {
//    val postIds = journalInput.posts.map (
//      post => post.id
//    )
//    val data = JournalData(JournalId(journalInput.id.toString), journalInput.name, postIds)
//    journalRepository.create(data).map {
//      id => createJournalResource(data)
//    }
//  }

  def create(journalInput: JournalResource): Future[JournalResource] = {
    val postIds = journalInput.posts.map (
      post => post.id
    )
    val data = JournalData(JournalId(journalInput.id.toString), journalInput.name, postIds)
    journalRepository.create(data).map {
      id => createJournalResource(data)
    }
  }

  def find(): Future[Iterable[JournalResource]] = {
    journalRepository.list().map {
      journalDataList => journalDataList.map(journalData => createJournalResource(journalData))
    }
  }

  def lookup(id: Int): Future[Option[JournalResource]] = {
    val journalFuture = journalRepository.lookup(JournalId(id.toString))
    journalFuture.map { maybeData => maybeData.map {
      data => createJournalResource(data) }
    }
  }

  private def createJournalResource(data: JournalData): JournalResource = {
    val postIdWithLinkList = ListBuffer.empty[PostIdWithLink]
    data.postIds.map(
      postId => postIdWithLinkList += PostIdWithLink(id = postId, link = routerProvider.get.link(PostId(postId.toString)))
    )
    JournalResource(data.name, data.id.toString.toInt, postIdWithLinkList.toList)
  }

//  private def createJournalResource(data: JournalData): JournalResource = {
//    JournalResource(data.name, data.id.toString.toInt, data.postIds)
//  }

}
