package actors

import akka.actor.{Actor, Props}
import models.Update

import scala.collection.mutable


object LastUpdatesActor{
  def props: Props = Props[LastUpdatesActor]

  sealed case class AddUpdate(update: Update)

  sealed case class GetLastUpdates()
  sealed case class ReceiveLastUpdates(updates: Seq[Update])
}

class LastUpdatesActor extends Actor{
  import actors.LastUpdatesActor._

  private val state: mutable.MutableList[Update] = mutable.MutableList.empty

  override def receive: Receive = {
    case AddUpdate(u) => state += u
    case GetLastUpdates() => sender ! ReceiveLastUpdates(state.takeRight(10))
  }
}
