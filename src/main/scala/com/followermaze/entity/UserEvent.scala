package com.followermaze.entity

sealed trait UserEvent

case class Subscribe(userId: String) extends UserEvent
case class Unsubscribe(userId: String) extends UserEvent
case object ClientDisconnected extends UserEvent
