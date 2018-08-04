import akka.actor.{Actor, ActorSystem, Props}
import com.typesafe.config.ConfigFactory


val classLoader = getClass.getClassLoader
val system = ActorSystem("MySystem", ConfigFactory.load(classLoader), classLoader)

object MyActor {
  def props = Props(new MyActor)
}
class MyActor extends Actor {
  override def receive = {
    case s: String => println(sender())
  }
}
val a = system.actorOf(MyActor.props, "deadLetters")

a ! "hi"