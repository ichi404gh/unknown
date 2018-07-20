package models

import play.api.libs.json._

object Chat {
  implicit val chatR: Reads[Chat] = (chat: JsValue) => {
    val t = (chat \ "type").validate[String].get
    t match {
      case "private" => Json.fromJson[PrivateChat](chat)
      case "group" => Json.fromJson[GroupChat](chat)
      case _ => JsError()
    }
  }
}

sealed trait Chat


case class User(
                 id: Long,
                 is_bot: Boolean,
                 first_name: Option[String],
                 last_name: Option[String],
                 username: String,
                 language_code: Option[String]
               )
object User  {
  implicit val userFormat: Reads[User] = Json.reads[User]
}

case class PrivateChat(
                        id: Long,
                        first_name: String,
                        last_name: Option[String],
                        username: String,
                      ) extends Chat
object PrivateChat {
  implicit val privateChatFormat: Reads[PrivateChat] = Json.reads[PrivateChat]
}

case class GroupChat(
                      id: Long,
                      title: String,
                      all_members_are_administrators: Boolean
                    ) extends Chat
object GroupChat {
  implicit val groupChaFormat: Reads[GroupChat] = Json.reads[GroupChat]
}

case class Message(
                    message_id: Long,
                    from: User,
                    chat: Chat,
                    date: Long,
                    text: Option[String]
                    //entities: Seq[Any],
                    //group_chat_created: Option[Boolean]
                  )
object Message {
  implicit val messageFormat: Reads[Message] = Json.reads[Message]
}

case class Update(update_id: Long, message: Option[Message])
object Update {
  implicit val updateFormat: Reads[Update] = Json.reads[Update]
}