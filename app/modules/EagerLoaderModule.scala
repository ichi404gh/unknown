package modules

import com.google.inject.AbstractModule
import services.StartUpService

// A Module is needed to register bindings
class EagerLoaderModule extends AbstractModule {
  override def configure() = {

    bind(classOf[StartUpService]).asEagerSingleton
  }
}