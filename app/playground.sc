import play.api.libs.json.{JsDefined, JsString, _}


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

sealed trait Chat

object Chat {

  //def unapply(arg: Chat): Option[JsObject] = JsValue("a"->"b")

  //  def apply(chat: JsObject) = {
  //    ((chat \ "type").toString match {
  //      case "private" => Json.fromJson[PrivateChat](chat)
  //      case "group" => Json.fromJson[GroupChat](chat)
  //    }).get
  //  }

  //  implicit val _ = Json.reads[Chat]
  implicit val chatR: Reads[Chat] = (chat: JsValue) => {
    val t = (chat \ "type").validate[String].get
    t match {
      case "private" => Json.fromJson[PrivateChat](chat)
      case "group" => Json.fromJson[GroupChat](chat)
      case _ => JsError()

    }
  }
}

val j =
  """
{
    "id":209941872,
    "first_name":"🅸🅲🅷🅸",
    "last_name":"4Ø4",
    "username":"ichi404",
    "type": "private"
}
  """



val p = Json.parse(j)
var res = Json.fromJson[Chat](p)
(p \ "type").validate[String].get


