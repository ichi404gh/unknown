package actors

import akka.actor.{Actor, Props}
import javax.inject.Inject
import models.{Message, Update}
import play.api.Logger
import services.TelegramService

import scala.collection.mutable



object UpdateProcessorActor {
  def props: Props = Props[UpdateProcessorActor]
}

class UpdateProcessorActor(tg: TelegramService) extends Actor {
  var queuedUser: Option[Long] = None
  val pairs: mutable.HashMap[Long, Long] = mutable.HashMap.empty

  override def receive: Receive = {
    case u: Update => process(u)
    case _ =>
  }

  private def process(update: Update): Unit = {
    update.message.foreach(message => {
      message.text match {
        case Some("/start") => tg.sendMessage(message.from.id, "Это бот для анонимного© общения. " +
          "Используй команду /next чтобы найти собеседника и /stop чтобы закончить диалог или выйти из очереди.")
        case Some("/next") => processNextCommand(message)
        case Some("/stop") => processStopCommand(message)
        case Some(text) => pairs.get(message.from.id) match {
            case Some(companionId) => tg.sendMessage(companionId, text)
            case None => tg.sendMessage(message.from.id, "Сначала найди собеседника. Отправь /next")
          }
        case _ =>
      }})
    Logger.info(queuedUser.toString)
    Logger.info(pairs.toString)
  }

  private def processStopCommand(message: Message): Unit = {
    val senderId = message.from.id
    if(pairs.contains(senderId))
      clearPairs(senderId)
    else
      queuedUser match {
        case Some(`senderId`) =>
          tg.sendMessage(senderId, "Ты вышел из очереди")
          queuedUser = None
        case _ =>
          tg.sendMessage(senderId, "Ты и так не в очереди")
    }
  }

  private def clearPairs(senderId: Long): Unit = {
    val companion = pairs.get(senderId)
    companion.foreach(id => {
      pairs -= id
      pairs -= senderId
      tg.sendMessage(id, "Собеседник прервал общение. Можно найти нового по команде /next")
      tg.sendMessage(senderId, "Вы прервали общение")
    })
  }

  private def processNextCommand(message: Message): Unit = {
    clearPairs(message.from.id)
    queuedUser match {
      case Some(id) =>
        if (id == message.from.id) {
          tg.sendMessage(message.from.id, "Ты уже в очереди")
          return
        }
        pairs += (id -> message.from.id)
        pairs += (message.from.id -> id)
        queuedUser = None
        tg.sendMessage(id, "Пара найдена, общайтес. Прервать общение можно командой /stop")
        tg.sendMessage(message.from.id, "Пара найдена, общайтес. Прервать общение можно командой /stop")
      case None =>
        queuedUser = Some(message.from.id)
        tg.sendMessage(message.from.id, "Ты в очереди, ждем собеседника")

    }
  }
}
