package controllers

import actors.LastUpdatesActor.{AddUpdate, GetLastUpdates, ReceiveLastUpdates}
import actors.{LastUpdatesActor, TestActor}
import actors.TestActor.{Inc, Pop}
import akka.actor.ActorSystem
import akka.pattern.ask
import javax.inject.{Inject, Singleton}
import models.Update
import play.Logger
import play.api.libs.json.Json
import play.api.Configuration
import play.api.mvc.{AbstractController, AnyContent, ControllerComponents, Request}

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.language.postfixOps
import scala.concurrent.duration._


@Singleton
class TelegramUpdateController @Inject()(cc: ControllerComponents, config: Configuration, system: ActorSystem)
  extends AbstractController(cc) {
  implicit val ec: ExecutionContext = ExecutionContext.global

  private val updatesActor = system.actorOf(LastUpdatesActor.props, "updates-actor")

  def receive(token: String) = Action { implicit request: Request[AnyContent] =>
    if (config.get[String]("telegram.token") != token)
      Ok("wrong token")

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
    updatesActor ! AddUpdate(update)
  }

  def index = Action.async {
    val fut = updatesActor.ask(GetLastUpdates())(1 seconds)
    fut.map{x=> x.asInstanceOf[ReceiveLastUpdates]}.map { x=>  Ok(x.toString) }
  }
}
