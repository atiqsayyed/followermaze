package com.followermaze.entity

sealed trait Event {
  val sequenceNo: Int
}

object Event {
  val orderedBySeq: Ordering[Event] = Ordering.by(_.sequenceNo)
  implicit def toInt(str: String) = str.trim.toInt

  def apply(eventMessage: String): Option[Event] = {
    eventMessage.split('|') match {
      case Array(sequenceNo, "F", from, to) =>
        Some(Follow(sequenceNo, from, to))
      case Array(sequenceNo, "U", from, to) =>
        Some(Unfollow(sequenceNo, from, to))
      case Array(sequenceNo, "B") => Some(Broadcast(sequenceNo))
      case Array(sequenceNo, "P", from, to) =>
        Some(Private(sequenceNo, from, to))
      case Array(sequenceNo, "S", from) => Some(StatusUpdate(sequenceNo, from))
      case _ => None
    }
  }
}

case class Broadcast(sequenceNo: Int) extends Event {
  override def toString = s"$sequenceNo|B"
}
case class Private(sequenceNo: Int, from: Int, to: Int) extends Event {
  override def toString = s"$sequenceNo|P|$from|$to"
}
case class Follow(sequenceNo: Int, from: Int, to: Int) extends Event {
  override def toString = s"$sequenceNo|F|$from|$to"
}
case class Unfollow(sequenceNo: Int, from: Int, to: Int) extends Event {
  override def toString = s"$sequenceNo|U|$from|$to"
}
case class StatusUpdate(sequenceNo: Int, from: Int) extends Event {
  override def toString = s"$sequenceNo|S|$from"
}
