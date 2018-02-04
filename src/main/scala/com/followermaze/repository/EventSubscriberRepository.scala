package com.followermaze.repository

import java.util.concurrent.ConcurrentHashMap

import com.followermaze.entity.Client
import com.followermaze.logger.Logger

import scala.collection.JavaConverters._

object EventSubscriberRepository extends Logger {

  private val eventSubscribers = new ConcurrentHashMap[Int, Client]().asScala

  def subscribe(clientId: Int, userClient: Client) = {
    eventSubscribers.put(clientId, userClient)
    log(
      s"client id $clientId subscribed, total subscription ${eventSubscribers.size}")
  }

}
