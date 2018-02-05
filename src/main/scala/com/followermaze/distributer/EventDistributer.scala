package com.followermaze.distributer

import com.followermaze.entity._
import com.followermaze.repository.{FollowerRepository, SubscriberRepository}
import com.followermaze.repository.EventQueue
import scala.annotation.tailrec
import scala.collection.mutable

class EventDistributer {
  private var nextSequenceToProcess: Int = 1
  private val orderedEventQueue =
    mutable.PriorityQueue()(Event.orderedBySeq.reverse)

  def enqueueEvent(inputMessage: String) = {
    Event(inputMessage).map(event => {
      EventQueue.enqueue(event)
    })
  }

  @tailrec
  final def startEventDistribution(): Unit = {
    val event = EventQueue.dequeue()
    distributeEvent(event)
    startEventDistribution()
  }

  private def distributeEvent(event: Event) = {
    orderedEventQueue += event
    while (hasNext) {
      val orderedEvent = orderedEventQueue.dequeue()
      if (orderedEvent.sequenceNo == nextSequenceToProcess)
        nextSequenceToProcess += 1
      orderedEvent match {
        case Follow(_, from, to) => {
          FollowerRepository.addFollower(to, from)
          notifySubscriber(to, orderedEvent)
        }
        case Unfollow(_, from, to) =>
          FollowerRepository.removeFollower(to, from)
        case Broadcast(sequenceNo) => notifyAll(orderedEvent)
        case Private(_, from, to) => notifySubscriber(to, orderedEvent)
        case StatusUpdate(_, from) => {
          val allFollowers = FollowerRepository.getAllFollowers(from)
          allFollowers.foreach(follower =>
            notifySubscriber(follower, orderedEvent))
        }
      }
    }
  }

  private def hasNext = {
    orderedEventQueue.nonEmpty && orderedEventQueue.head.sequenceNo <= nextSequenceToProcess
  }

  private def notifySubscriber(to: Int, event: Event) = {
    SubscriberRepository.getSubscriber(to).foreach(_.notify(event))
  }

  private def notifyAll(event: Event) = {
    SubscriberRepository.getAllSubscribers.foreach(_.notify(event))
  }

  def shutdown = {
    EventQueue.clear()
    SubscriberRepository.getAllSubscribers.foreach(_.shutdown())
  }

}
