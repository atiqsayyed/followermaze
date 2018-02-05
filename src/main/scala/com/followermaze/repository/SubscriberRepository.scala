package com.followermaze.repository

import com.followermaze.entity.Subscriber

import scala.collection.mutable

object SubscriberRepository {

  private val subscribers = mutable.Map[Int, Subscriber]()

  def addSubscriber(clientId: Int, userClient: Subscriber) = {
    subscribers.put(clientId, userClient)
  }

  def getSubscriber(clientId: Int): Option[Subscriber] = {
    subscribers.get(clientId)
  }

  def getAllSubscribers: Iterable[Subscriber] = {
    subscribers.values
  }

  def clear() = {
    subscribers.clear()
  }

}
