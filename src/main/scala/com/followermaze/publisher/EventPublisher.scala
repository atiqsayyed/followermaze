package com.followermaze.publisher

import java.util.concurrent.LinkedBlockingQueue

import com.followermaze.entity._
import com.followermaze.repository.{FollowerRepository, SubscriberRepository}

import scala.annotation.tailrec
import scala.collection.mutable

class EventPublisher {
  private var nextSeq: Int = 1
  private val eventQueue: mutable.PriorityQueue[Event] =
    mutable.PriorityQueue()(Event.orderedBySeq.reverse)
  private val incomingEventQueue = new LinkedBlockingQueue[Event]()

  def publish(inputMessage: String) = {
    Event(inputMessage).map(event => {
      incomingEventQueue.add(event)
    })
  }

  @tailrec
  final def startPublishing(): Unit = {
    val event = incomingEventQueue.take()
    distributeEvent(event)
    startPublishing()
  }

  private def distributeEvent(event: Event) = {
    eventQueue += event
    while (hasNext) {
      val orderedEvent = eventQueue.dequeue()
      if (orderedEvent.sequenceNo == nextSeq) nextSeq += 1
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
    eventQueue.nonEmpty && eventQueue.head.sequenceNo <= nextSeq
  }

  private def notifySubscriber(to: Int, event: Event) = {
    SubscriberRepository.getSubscriber(to).foreach(_.notify(event))
  }

  private def notifyAll(event: Event) = {
    SubscriberRepository.getAllSubscribers.foreach(_.notify(event))
  }

  def shutdown = {
    SubscriberRepository.getAllSubscribers.foreach(_.shutdown())
  }

}
