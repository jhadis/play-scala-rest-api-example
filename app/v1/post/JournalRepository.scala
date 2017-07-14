package v1.post

import javax.inject.{Inject, Singleton}

import com.google.inject.ImplementedBy

import scala.collection.mutable.ListBuffer
import scala.concurrent.Future

/**
 * Created by admin on 6/15/17.
 */

final case class JournalData(id: JournalId, name: String, postIds: List[Int])

class JournalId private (val underlying: Int) extends AnyVal {
  override def toString: String = underlying.toString
}

object JournalId {
  def apply(raw: String): JournalId = {
    require(raw != null)
    new JournalId(Integer.parseInt(raw))
  }
}

//@ImplementedBy(classOf[JournalRepositoryImpl])
trait JournalRepository {
  def create(data: JournalData): Future[JournalId]

  def list(): Future[Iterable[JournalData]]

  def lookup(id: JournalId): Future[Option[JournalData]]
}

@Singleton
class JournalRepositoryImpl @Inject() extends JournalRepository {

  private val logger = org.slf4j.LoggerFactory.getLogger(this.getClass)
  private val journals = ListBuffer.empty[JournalData]

  override def create(data: JournalData): Future[JournalId] = {
    Future.successful {
      logger.trace(s"create $data")
      this.journals += data
      data.id
    }
  }

  override def lookup(id: JournalId): Future[Option[JournalData]] = {
    Future.successful {
      logger.trace(s"Find journal; id = $id")
      this.journals.find(journal => journal.id == id)
    }
  }

  override def list(): Future[Iterable[JournalData]] = {
    Future.successful {
      logger.trace("List all journals")
//      journals.toList.toIterable
//      journals.toIterable
//      Iterable.apply(journals)
      this.journals
    }
  }
}
