package services

import javax.inject.{Inject, Singleton}
import play.api.{Configuration, Logger}
import scalaj.http.Http

@Singleton
class TelegramService @Inject() (config: Configuration) {

  def sendMessage(toId: Long, message: String): Unit = {
    call("sendMessage", Seq(
      "chat_id" -> toId.toString,
      "text" -> message
    ))
  }

  private def call(method: String, params: Seq[(String,String)]) = {
    val token = config.get[String]("telegram.token")

    Http(s"https://api.telegram.org/bot$token/$method")
      .params(params).postForm.asString
  }
}
