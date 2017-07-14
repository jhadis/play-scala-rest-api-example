package v1

import javax.inject.{Provider, Singleton}

import com.google.inject.{Guice, Key, PrivateModule, AbstractModule}
import net.codingwell.scalaguice.{ScalaPrivateModule, ScalaModule}
import v1.post._

/**
 * Created by admin on 6/17/17.
 */
class MyModule extends AbstractModule
with ScalaModule {
  override def configure() = {
    bind[PostRepository].to[PostRepositoryImpl].in[Singleton]
    bind[JournalRepository].to[JournalRepositoryImpl].in[Singleton]
//    bind[PostRouter].to[PostRouterImpl]
    bind[PostController]
    val injector = Guice.createInjector(this)

    import net.codingwell.scalaguice.InjectorExtensions._
    val controller = injector.instance[PostController]
    val instance = new PostRouterImpl(controller)
    bind(classOf[PostRouter]).toInstance(instance)
  }
}

class MyPrivateModule extends PrivateModule with ScalaPrivateModule {
  def configure(): Unit = {
    bind[PostRouter].to[PostRouterImpl]
    expose[PostRouter]
  }
}
