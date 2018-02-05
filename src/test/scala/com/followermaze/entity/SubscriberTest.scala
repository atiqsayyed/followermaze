package com.followermaze.entity

import java.io.ByteArrayOutputStream

import org.scalatest.{Matchers, WordSpec}

class SubscriberTest extends WordSpec with Matchers {

  "Subscriber" should {
    "notify the output stream with same order" in {
      val outputStream = new ByteArrayOutputStream
      val subscriber = new Subscriber(1, outputStream)
      val events = List(Follow(1, 2, 3), Broadcast(2), Unfollow(2, 5, 7))

      events.foreach(subscriber.notify)

      outputStream.toString.trim should be(events.mkString("\r\n"))
    }
  }

}
