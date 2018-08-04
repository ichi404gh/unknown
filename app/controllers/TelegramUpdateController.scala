package controllers

import actors.{LastUpdatesActor, UpdateProcessorActor}
import actors.LastUpdatesActor.{AddUpdate, GetLastUpdates, ReceiveLastUpdates}
import akka.actor.{ActorSystem, Props}
import akka.pattern.ask
import javax.inject.{Inject, Singleton}
import models.Update
import play.api.{Configuration, Logger}
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}
import services.TelegramService

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.language.postfixOps


@Singleton
class TelegramUpdateController @Inject()(
                                          cc: ControllerComponents,
                                          config: Configuration,
                                          system: ActorSystem,
                                          tg: TelegramService
                                        )
  extends AbstractController(cc) {
  implicit val ec: ExecutionContext = ExecutionContext.global

  private val updateProcessor = system.actorOf(Props(new UpdateProcessorActor(tg)), "updates-processor")

  def receive(token: String) = Action { implicit request: Request[AnyContent] =>
    if (config.get[String]("telegram.token") != token) {
      Logger.warn(s"trying to access with wrong token: $token")
      Ok("wrong token")
    }
    else {

      val body = request.body.asJson
      body match  {
        case Some(b) => process(Json.fromJson[Update](b).get)
        case _ =>
      }

      Ok("")
    }
  }


  def process(update: Update): Unit = {
    updateProcessor ! update
    Logger.debug(s"${update.message.map(_.from.username)} : ${update.message.flatMap(_.text)}")
  }
}
