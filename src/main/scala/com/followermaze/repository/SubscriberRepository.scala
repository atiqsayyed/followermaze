package com.followermaze.repository

import com.followermaze.entity.Subscriber
import com.followermaze.logger.Logger

import scala.collection.mutable

object SubscriberRepository extends Logger {

  private val subscribers = mutable.Map[Int, Subscriber]()

  def subscribe(clientId: Int, userClient: Subscriber) = {
    subscribers.put(clientId, userClient)
    log(
      s"client id $clientId subscribed, total subscription ${subscribers.size}")
  }

  def getSubscriber(clientId: Int): Option[Subscriber] = {
    subscribers.get(clientId)
  }

  def getAllSubscribers: Iterable[Subscriber] = {
    subscribers.values
  }

}
