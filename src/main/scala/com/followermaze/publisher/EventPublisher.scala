package com.followermaze.publisher

import java.util.concurrent.LinkedBlockingQueue

import com.followermaze.entity._
import com.followermaze.repository.{FollowerRepository, SubscriberRepository}

import scala.annotation.tailrec
import scala.collection.mutable
import scala.concurrent.ExecutionContext

class EventPublisher() {
  private var nextSeq: Int = 1
  private val eventQueue: mutable.PriorityQueue[Event] =
    mutable.PriorityQueue()(Event.orderedBySeq.reverse)
  val incomingEventQueue = new LinkedBlockingQueue[Event]()

  def handleEvent(inputMessage: String)(implicit ex: ExecutionContext) = {
    Event(inputMessage).map(event => {
      incomingEventQueue.add(event)
    })
  }

  private def publish(event: Event) = {
    eventQueue += event
    while (eventQueue.nonEmpty && eventQueue.head.sequenceNo <= nextSeq) {
      val orderedEvent = eventQueue.dequeue()
      println(s"dequeue: $orderedEvent")
      if (orderedEvent.sequenceNo == nextSeq) nextSeq += 1
      orderedEvent match {
        case Follow(_, from, to) => {
          FollowerRepository.addFollower(to, from)
          send(to, orderedEvent)
        }
        case Unfollow(_, from, to) =>
          FollowerRepository.removeFollower(to, from)
        case Broadcast(sequenceNo) => send(orderedEvent)
        case Private(_, from, to) => send(to, orderedEvent)
        case StatusUpdate(_, from) => {
          val allFollowers = FollowerRepository.getAllFollowers(from)
          allFollowers.foreach(existingFollowers => {
            existingFollowers.foreach(follower => send(follower, orderedEvent))
          })
        }
      }
    }
  }

  @tailrec
  final def startPublishing()(implicit ex: ExecutionContext): Unit = {
    val event = incomingEventQueue.take()
    publish(event)
    startPublishing()
  }

  private def send(to: Int, event: Event) = {
    SubscriberRepository.getSubscriber(to).foreach(_.sendMessage(event))
  }

  private def send(event: Event) = {
    SubscriberRepository.getAllSubscribers.foreach(_.sendMessage(event))
  }

  def shutdown = {
    SubscriberRepository.getAllSubscribers.foreach(_.shutdown())
  }

}
