package com.followermaze.entity

trait EventType {
  def encodedType: String
}

object EventType {
  implicit def toInt(str: String) = str.toInt

  def apply(eventMessage: String): EventType = {
    val eventCodes = eventMessage.split("\\|")
    eventCodes(1) match {
      case "B" => Broadcast(eventCodes(0))
      case "P" => Private(eventCodes(0), eventCodes(2), eventCodes(3))
      case "F" => Follow(eventCodes(0), eventCodes(2), eventCodes(3))
      case "U" => Unfollow(eventCodes(0), eventCodes(2), eventCodes(3))
      case "S" => Unfollow(eventCodes(0), eventCodes(2), eventCodes(3))
    }

  }
}

case class Broadcast(sequenceNo: Int) extends EventType {
  override def encodedType = s"$sequenceNo|B"
}
case class Private(sequenceNo: Int, source: String, destination: String)
    extends EventType {
  override def encodedType = s"$sequenceNo|P|$source|$destination"
}
case class Follow(sequenceNo: Int, source: String, destination: String)
    extends EventType {
  override def encodedType = s"$sequenceNo|F|$source|$destination"
}
case class Unfollow(sequenceNo: Int, source: String, destination: String)
    extends EventType {
  override def encodedType = s"$sequenceNo|U|$source|$destination"
}
case class StatusUpdate(sequenceNo: Int, source: String) extends EventType {
  override def encodedType = s"$sequenceNo|S|$source"
}
