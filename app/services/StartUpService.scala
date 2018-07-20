package services

import javax.inject.{Inject, Singleton}
import play.api.Configuration
import scalaj.http.{Http, HttpResponse}
import play.api.Logger

@Singleton
class StartUpService @Inject()(config: Configuration) {

  val token: String = config.get[String]("telegram.token")
  val host: String = config.get[String]("telegram.receiveHost")

  Logger.info(s"Using host: $host")
  val resp: HttpResponse[String] = Http(s"https://api.telegram.org/bot$token/setWebhook")
    .param("url", s"$host/updates/$token").asString

  println(resp.body)
}