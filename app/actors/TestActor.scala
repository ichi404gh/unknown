package actors

import akka.actor.{Actor, Props}

object TestActor {
  def props = Props[TestActor]

  case class Inc()
  case class Pop()


}
class TestActor extends Actor{
  import actors.TestActor._

  private var counter = 0

  override def receive: Receive = {
    case Inc() => counter = counter + 1
    case Pop()=> sender ! counter
  }
}
