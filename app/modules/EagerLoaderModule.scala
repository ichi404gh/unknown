package modules

import play.api.{Configuration, Environment}
import play.api.inject._
import services.{StartUpService, TelegramService}
// A Module is needed to register bindings
class EagerLoaderModule extends Module {
  override def bindings(environment: Environment,
                        configuration: Configuration): Seq[Binding[_]] = Seq(
    bind(classOf[StartUpService]).toSelf,
    bind(classOf[TelegramService]).toSelf,
  )
}